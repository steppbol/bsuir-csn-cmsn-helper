package com.bsuir.sdtt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Class of order that extends BaseEntity class.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    private String imageId;

    private String compressedImageId;

    @NotNull
    private UUID customerId;

    @NotNull
    private String name;

    /**
     * Field of customer email.
     */
    @Email
    @NotNull
    private String email;

    /**
     * Field of order total price;
     */
    @Min(0)
    @NotNull
    private double totalPrice;

    /**
     * Field of order item count.
     */
    @Min(0)
    @NotNull
    private int orderItemCount;


    /**
     * Constructor without params that create object without initialization fields.
     */
    public Order() {
    }
}