package com.bsuir.sdtt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Class of offer comments that extends BaseEntity class.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@Entity
public class Comment extends BaseEntity {
    @NotNull
    private UUID customerId;

    @NotNull
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private Offer offer;

    public Comment() {

    }
}
