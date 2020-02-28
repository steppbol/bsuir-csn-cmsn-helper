package com.bsuir.sdtt.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Class of Comment Data Transfer Object. Used to transfer data between application subsystems.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private UUID id;

    @NotNull
    private UUID customerId;

    @NotNull
    private String message;
}
