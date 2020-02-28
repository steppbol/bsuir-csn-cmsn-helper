package com.bsuir.sdtt.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Class of base entity that contains base features that implements Cloneable interface.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@MappedSuperclass
public abstract class BaseEntity implements Cloneable {
    /**
     * Field of entity UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id.toString() +
                '}';
    }
}
