package com.bsuir.sdtt.repository;

import com.bsuir.sdtt.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Interface of Order repository that extends CrudRepository. Contains CRUD methods.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> {
    List<Order> findAllByCustomerId(UUID customerId);
}
