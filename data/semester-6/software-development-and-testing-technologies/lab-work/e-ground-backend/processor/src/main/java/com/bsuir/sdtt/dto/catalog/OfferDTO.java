package com.bsuir.sdtt.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class of Offer Data Transfer Object. Used to transfer data between application subsystems.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {
    private UUID id;

    private String imageId;

    private String compressedImageId;

    private String image;

    /**
     * Field of seller ID.
     */
    @NotNull
    private UUID sellerId;

    /**
     * Field of Offer firstName.
     */
    @NotNull
    private String name;

    /**
     * Field of Offer category.
     */
    @NotNull
    private String category;

    @NotNull
    private String description;

    /**
     * Field of Offer price.
     */
    @NotNull
    @Min(0)
    private double price;

    private List<CommentDTO> comments = new ArrayList<>();
}
