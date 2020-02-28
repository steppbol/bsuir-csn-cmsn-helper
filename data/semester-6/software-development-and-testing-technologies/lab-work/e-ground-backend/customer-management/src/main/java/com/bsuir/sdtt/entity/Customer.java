package com.bsuir.sdtt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * Class of customer that extends BaseEntity class.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
    private String imageId;

    private String compressedImageId;

    /**
     * Field of customer firstName.
     */
    @Basic(optional = false)
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
    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    /**
     * Field of customer age.
     */
    @Min(1)
    @NotNull
    private int age;

    /**
     * Field of customer number
     */
    @NotNull
    @Column(unique = true)
    @Pattern(regexp = "^\\+375(29|33|44)\\d{7}$")
    private String phoneNumber;

    @OneToMany(mappedBy = "secondAccount", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Conversation> conversationList;

    @OneToMany(mappedBy = "secondAccount", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Conversation> otherConversationList;

    /**
     * Constructor without params that create object without initialization fields.
     */
    public Customer() {
    }

    /**
     * Method that set values except the password from another customer
     *
     * @param customer
     */
    public void update(Customer customer) {
        firstName = customer.firstName;
        lastName = customer.lastName;
        email = customer.email;
        age = customer.age;
        phoneNumber = customer.phoneNumber;
    }
}
