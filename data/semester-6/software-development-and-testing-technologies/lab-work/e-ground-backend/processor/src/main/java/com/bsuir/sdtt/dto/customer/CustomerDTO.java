package com.bsuir.sdtt.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

/**
 * Class of Customer Data Transfer Object.
 * Used to transfer data between application subsystems.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private UUID id;

    private String imageId;

    private String compressedImageId;

    private String image;

    /**
     * Field of customer firstName.
     */
    @NotNull
    private String firstName;

    /**
     * Field of customer lastName.
     */
    @NotNull
    private String lastName;

    /**
     * Field of customer email.
     */
    @NotNull
    @Email
    private String email;

    /**
     * Field of customer age.
     */
    @NotNull
    @Min(1)
    private int age;

    @NotNull
    private String password;

    /**
     * Field of customer number
     */
    @NotNull
    @Pattern(regexp = "^\\+375(29|33|44)\\d{7}$")
    private String phoneNumber;
}
