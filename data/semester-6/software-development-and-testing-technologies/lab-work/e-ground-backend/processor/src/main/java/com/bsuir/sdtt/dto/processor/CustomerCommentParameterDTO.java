package com.bsuir.sdtt.dto.processor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Class of Customer Comment Parameter.
 * Used to create order from offer.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCommentParameterDTO {
    private UUID id;

    @NotNull
    private String message;

    @NotNull
    private AccountDTO accountDto;
}
