package com.bsuir.sdtt.repository;

import com.bsuir.sdtt.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query(value = "SELECT * FROM conversation c WHERE c.id_your_account=:userId OR c.id_other_account=:userId",
            nativeQuery = true)
    List<Conversation> getUserConversationsByUserId(@Param("userId") UUID userId);

    @Query(value = "SELECT * FROM conversation c WHERE c.id_your_account=:yourId AND c.id_other_account=:otherId OR " +
            "c.id_your_account=:otherId AND c.id_other_account=:yourId", nativeQuery = true)
    Optional<Conversation> getConversationByUsersIds(@Param("yourId") UUID yourId, @Param("otherId") UUID otherId);

    Optional<Conversation> findConversationById(UUID conversationId);
}
