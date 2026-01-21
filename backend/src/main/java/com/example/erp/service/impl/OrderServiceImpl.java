package com.example.erp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.OrderItemDTO;
import com.example.erp.dto.OrderRequestDTO;
import com.example.erp.dto.OrderResponseDTO;
import com.example.erp.entity.Inventory;
import com.example.erp.entity.InventoryLog;
import com.example.erp.entity.Order;
import com.example.erp.entity.OrderItem;
import com.example.erp.repository.InventoryLogRepository;
import com.example.erp.repository.InventoryRepository;
import com.example.erp.repository.OrderItemRepository;
import com.example.erp.repository.OrderRepository;
import com.example.erp.service.OrderService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            InventoryRepository inventoryRepository,
                            InventoryLogRepository inventoryLogRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    // ================= CREATE ORDER =================
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setWarehouse(dto.getWarehouse());
        order.setCode("ORD-" + System.currentTimeMillis());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = dto.getItems().stream().map(i -> {

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(i.getProductId(), dto.getWarehouse())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Inventory not found"));

            if (inventory.getAvailableQuantity() < i.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for product ID " + i.getProductId());
            }

            // trừ tồn
            inventory.setQuantity(inventory.getQuantity() - i.getQuantity());
            inventoryRepository.save(inventory);

            // log tồn kho
            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(-i.getQuantity());
            log.setType("OUT");
            log.setWarehouse(dto.getWarehouse());
            inventoryLogRepository.save(log);

            // order item
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProductId(i.getProductId());
            item.setProductName(inventory.getProductName());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;

        }).collect(Collectors.toList());

        orderItemRepository.saveAll(items);
        return toResponseDTO(savedOrder, items);
    }

    // ================= GET ORDER =================
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        return toResponseDTO(order, items);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(o -> toResponseDTO(
                        o,
                        orderItemRepository.findByOrderId(o.getId())))
                .collect(Collectors.toList());
    }

    // ================= UPDATE ORDER =================
    @Override
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // hoàn tồn cũ
        List<OrderItem> oldItems = orderItemRepository.findByOrderId(id);
        for (OrderItem i : oldItems) {

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(i.getProductId(), order.getWarehouse())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Inventory not found"));

            inventory.setQuantity(inventory.getQuantity() + i.getQuantity());
            inventoryRepository.save(inventory);

            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(i.getQuantity());
            log.setType("RETURN");
            log.setWarehouse(order.getWarehouse());
            inventoryLogRepository.save(log);
        }

        orderItemRepository.deleteAll(oldItems);

        // thêm item mới
        List<OrderItem> newItems = dto.getItems().stream().map(i -> {

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(i.getProductId(), order.getWarehouse())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Inventory not found"));

            if (inventory.getAvailableQuantity() < i.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for product ID " + i.getProductId());
            }

            inventory.setQuantity(inventory.getQuantity() - i.getQuantity());
            inventoryRepository.save(inventory);

            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(-i.getQuantity());
            log.setType("OUT");
            log.setWarehouse(order.getWarehouse());
            inventoryLogRepository.save(log);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(i.getProductId());
            item.setProductName(inventory.getProductName());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;

        }).collect(Collectors.toList());

        orderItemRepository.saveAll(newItems);
        return toResponseDTO(order, newItems);
    }

    // ================= CANCEL ORDER =================
    @Override
    public void cancelOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if ("CANCELLED".equals(order.getStatus())) return;

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        for (OrderItem i : items) {

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(i.getProductId(), order.getWarehouse())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Inventory not found"));

            inventory.setQuantity(inventory.getQuantity() + i.getQuantity());
            inventoryRepository.save(inventory);

            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(i.getQuantity());
            log.setType("RETURN");
            log.setWarehouse(order.getWarehouse());
            inventoryLogRepository.save(log);
        }
    }

    // ================= CONFIRM ORDER =================
    @Override
    public void confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus("CONFIRMED");
        orderRepository.save(order);
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
