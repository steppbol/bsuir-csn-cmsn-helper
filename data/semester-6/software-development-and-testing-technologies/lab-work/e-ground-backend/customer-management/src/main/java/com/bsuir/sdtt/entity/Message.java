package com.bsuir.sdtt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "message")
public class Message extends BaseEntity implements Comparable<Message> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Customer sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Customer receiver;

    private String body;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Override
    public int compareTo(Message o) {
        if (getCreationDate() == null || o.getCreationDate() == null) {
            return 0;
        }
        return getCreationDate().compareTo(o.getCreationDate());
    }
}

