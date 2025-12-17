package com.example.erp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.erp.dto.OrderRequestDTO;
import com.example.erp.dto.OrderResponseDTO;
import com.example.erp.dto.OrderItemDTO;
import com.example.erp.entity.Order;
import com.example.erp.entity.OrderItem;
import com.example.erp.entity.Inventory;
import com.example.erp.entity.InventoryLog;
import com.example.erp.repository.OrderRepository;
import com.example.erp.repository.OrderItemRepository;
import com.example.erp.repository.InventoryRepository;
import com.example.erp.repository.InventoryLogRepository;
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

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setCode("ORD-" + System.currentTimeMillis());

        Order savedOrder = orderRepository.save(order);

        // Tạo item & trừ tồn kho
        List<OrderItem> items = dto.getItems().stream().map(i -> {
            // kiểm tra tồn kho
            Inventory inventory = inventoryRepository.findByProductId(i.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

            if (inventory.getAvailableQuantity() < i.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product ID " + i.getProductId());
            }

            inventory.setQuantity(inventory.getQuantity() - i.getQuantity());
            inventoryRepository.save(inventory);

            // tạo inventory log
            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(-i.getQuantity());
            log.setType("OUT");
            log.setWarehouse(inventory.getWarehouse());
            inventoryLogRepository.save(log);

            // tạo order item
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(items);

        return toResponseDTO(savedOrder, items);
    }

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
                .map(o -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
                    return toResponseDTO(o, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // trả lại tồn cũ
        List<OrderItem> oldItems = orderItemRepository.findByOrderId(id);
        oldItems.forEach(i -> {
            Inventory inv = inventoryRepository.findByProductId(i.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
            inv.setQuantity(inv.getQuantity() + i.getQuantity());
            inventoryRepository.save(inv);

            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(i.getQuantity());
            log.setType("RETURN");
            log.setWarehouse(inv.getWarehouse());
            inventoryLogRepository.save(log);
        });
        orderItemRepository.deleteAll(oldItems);

        // thêm item mới
        List<OrderItem> items = dto.getItems().stream().map(i -> {
            Inventory inventory = inventoryRepository.findByProductId(i.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

            if (inventory.getAvailableQuantity() < i.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product ID " + i.getProductId());
            }

            inventory.setQuantity(inventory.getQuantity() - i.getQuantity());
            inventoryRepository.save(inventory);

            InventoryLog log = new InventoryLog();
            log.setProductId(i.getProductId());
            log.setQuantityChange(-i.getQuantity());
            log.setType("OUT");
            log.setWarehouse(inventory.getWarehouse());
            inventoryLogRepository.save(log);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(items);

        return toResponseDTO(order, items);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getStatus().equals("CANCELLED")) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);

            // trả tồn kho
            List<OrderItem> items = orderItemRepository.findByOrderId(id);
            items.forEach(i -> {
                Inventory inventory = inventoryRepository.findByProductId(i.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
                inventory.setQuantity(inventory.getQuantity() + i.getQuantity());
                inventoryRepository.save(inventory);

                InventoryLog log = new InventoryLog();
                log.setProductId(i.getProductId());
                log.setQuantityChange(i.getQuantity());
                log.setType("RETURN");
                log.setWarehouse(inventory.getWarehouse());
                inventoryLogRepository.save(log);
            });
        }
    }

    @Override
    public void confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus("CONFIRMED");
        orderRepository.save(order);
    }

    private OrderResponseDTO toResponseDTO(Order order, List<OrderItem> items) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCode(order.getCode());
        dto.setCustomerId(order.getCustomerId());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setItems(items.stream().map(i -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductId(i.getProductId());
            itemDTO.setQuantity(i.getQuantity());
            itemDTO.setPrice(i.getPrice());
            return itemDTO;
        }).collect(Collectors.toList()));
        return dto;
    }
}
