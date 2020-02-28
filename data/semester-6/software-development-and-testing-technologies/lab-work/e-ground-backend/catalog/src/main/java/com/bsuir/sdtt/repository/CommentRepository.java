package com.bsuir.sdtt.repository;

import com.bsuir.sdtt.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, UUID> {
    List<Comment> findAllByOfferId(UUID offerId);
}
