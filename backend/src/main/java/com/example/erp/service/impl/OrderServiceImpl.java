package com.example.erp.service.impl;

import com.example.erp.dto.*;
import com.example.erp.entity.*;
import com.example.erp.repository.*;
import com.example.erp.service.OrderService;
import com.example.erp.util.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryRepository inventoryRepository,
            InventoryLogRepository inventoryLogRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    // ================= CREATE ORDER (KHÔNG TRỪ KHO) =================
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);
        order.setWarehouse("DEFAULT");
        order.setCode("ORD-" + System.currentTimeMillis());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequestDTO req : dto.getItems()) {

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(req.getProductId(), savedOrder.getWarehouse())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Inventory not found for productId=" + req.getProductId())
                    );

            // ✅ Chỉ kiểm tra tồn
            if (inventory.getQuantity() < req.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for productId=" + req.getProductId());
            }

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProductId(req.getProductId());
            item.setProductName(inventory.getProductName());
            item.setQuantity(req.getQuantity());
            item.setPrice(req.getPrice());

            items.add(item);
        }

        orderItemRepository.saveAll(items);
        return toResponseDTO(savedOrder, items);
    }

    // ================= GET =================
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        return toResponseDTO(order, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(o -> toResponseDTO(o, orderItemRepository.findByOrderId(o.getId())))
                .collect(Collectors.toList());
    }

    // ================= UPDATE ORDER (HOÀN KHO → TRỪ LẠI) =================
    @Override
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update cancelled order");
        }

        // 🔁 Hoàn kho item cũ
        List<OrderItem> oldItems = orderItemRepository.findByOrderId(id);
        for (OrderItem i : oldItems) {
            Inventory inv = getInventory(i.getProductId(), order.getWarehouse());
            inv.setQuantity(inv.getQuantity() + i.getQuantity());
            inventoryRepository.save(inv);

            saveInventoryLog(i.getProductId(), i.getQuantity(), "RETURN", order.getWarehouse());
        }

        orderItemRepository.deleteAll(oldItems);

        // ➕ Trừ kho item mới
        List<OrderItem> newItems = new ArrayList<>();

        for (OrderItemRequestDTO req : dto.getItems()) {

            Inventory inv = getInventory(req.getProductId(), order.getWarehouse());

            if (inv.getQuantity() < req.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for productId=" + req.getProductId());
            }

            inv.setQuantity(inv.getQuantity() - req.getQuantity());
            inventoryRepository.save(inv);

            saveInventoryLog(req.getProductId(), -req.getQuantity(), "OUT", order.getWarehouse());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(req.getProductId());
            item.setProductName(inv.getProductName());
            item.setQuantity(req.getQuantity());
            item.setPrice(req.getPrice());

            newItems.add(item);
        }

        orderItemRepository.saveAll(newItems);
        return toResponseDTO(order, newItems);
    }

    // ================= CANCEL ORDER (HOÀN KHO) =================
    @Override
    public void cancelOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) return;

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        for (OrderItem i : items) {

            Inventory inv = getInventory(i.getProductId(), order.getWarehouse());
            inv.setQuantity(inv.getQuantity() + i.getQuantity());
            inventoryRepository.save(inv);

            saveInventoryLog(i.getProductId(), i.getQuantity(), "RETURN", order.getWarehouse());
        }
    }

    // ================= CONFIRM ORDER (TRỪ KHO) =================
    @Override
    public void confirmOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING order can be confirmed");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(id);

        for (OrderItem i : items) {

            Inventory inv = getInventory(i.getProductId(), order.getWarehouse());

            if (inv.getQuantity() < i.getQuantity()) {
                throw new IllegalStateException("Not enough stock when confirm order");
            }

            inv.setQuantity(inv.getQuantity() - i.getQuantity());
            inventoryRepository.save(inv);

            saveInventoryLog(i.getProductId(), -i.getQuantity(), "OUT", order.getWarehouse());
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    // ================= HELPER =================
    private Inventory getInventory(Long productId, String warehouse) {
        return inventoryRepository
                .findByProductIdAndWarehouse(productId, warehouse)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
    }

    private void saveInventoryLog(Long productId, int qty, String type, String warehouse) {
        InventoryLog log = new InventoryLog();
        log.setProductId(productId);
        log.setQuantityChange(qty);
        log.setType(type);
        log.setWarehouse(warehouse);
        inventoryLogRepository.save(log);
    }

    // ================= MAPPER =================
    private OrderResponseDTO toResponseDTO(Order order, List<OrderItem> items) {

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCode(order.getCode());
        dto.setCustomerId(order.getCustomerId());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedDate(order.getCreatedDate());

        dto.setItems(items.stream().map(i -> {
            OrderItemDTO d = new OrderItemDTO();
            d.setProductId(i.getProductId());
            d.setProductName(i.getProductName());
            d.setQuantity(i.getQuantity());
            d.setPrice(i.getPrice());
            return d;
        }).collect(Collectors.toList()));

        return dto;
    }
}
