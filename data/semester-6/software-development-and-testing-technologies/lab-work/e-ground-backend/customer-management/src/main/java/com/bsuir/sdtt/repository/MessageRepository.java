package com.bsuir.sdtt.repository;

import com.bsuir.sdtt.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query(value = "SELECT * FROM message m WHERE m.conversation_id=:conversationId ORDER BY m.creation_date ASC", nativeQuery = true)
    List<Message> getConversationMessagesById(@Param("conversationId") UUID conversationId);

    @Query(value = "SELECT * FROM message m WHERE (m.conversation_id, m.creation_date) IN " +
            "(SELECT conversation_id, MAX(creation_date) FROM message GROUP BY conversation_id HAVING conversation_id=:conversationId)",
            nativeQuery = true)
    Optional<Message> getConversationLastMessageById(@Param("conversationId") UUID conversationId);
}