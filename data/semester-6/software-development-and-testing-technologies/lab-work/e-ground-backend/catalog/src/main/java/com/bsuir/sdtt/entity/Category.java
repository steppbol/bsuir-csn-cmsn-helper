package com.bsuir.sdtt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Class of offer category that extends BaseEntity class.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@Entity
public class Category extends BaseEntity {
    /**
     * Necessarily field of Category firstName.
     */
    @Basic(optional = false)
    @NotNull
    private String name;

    /**
     * Field of offers whose category.
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @NotNull
    private List<Offer> offers = new ArrayList<>();

    /**
     * Constructor without params that create object without initialization fields.
     */
    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }
}