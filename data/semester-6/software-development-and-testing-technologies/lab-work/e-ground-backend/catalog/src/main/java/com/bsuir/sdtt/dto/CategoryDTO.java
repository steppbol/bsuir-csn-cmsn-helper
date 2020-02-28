package com.bsuir.sdtt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Class of Category Data Transfer Object. Used to transfer data between application subsystems.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
public class CategoryDTO {
    private UUID id;

    /**
     * Field of Category firstName
     */
    @NotNull
    private String name;

    public CategoryDTO() {

    }
}
