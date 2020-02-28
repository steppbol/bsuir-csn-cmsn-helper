package com.bsuir.sdtt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;

    @ManyToOne
    @JoinColumn(name = "id_your_account", nullable = false)
    private Customer firstAccount;

    @ManyToOne
    @JoinColumn(name = "id_other_account", nullable = false)
    private Customer secondAccount;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "conversation", allowSetters = true)
    List<Message> messages;
}