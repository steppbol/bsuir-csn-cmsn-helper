package com.bsuir.sdtt.service;

import com.bsuir.sdtt.entity.Order;

import java.util.List;
import java.util.UUID;

/**
 * Interface of order service. Contains CRUD methods.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
public interface OrderService {
    Order create(Order order);

    List<Order> findAll();

    Order findById(UUID id);

    List<Order> findByCustomerId(UUID customerId);

    Order update(Order order);

    void delete(UUID id);
}
