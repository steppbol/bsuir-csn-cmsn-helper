package com.bsuir.sdtt.service;

import com.bsuir.sdtt.entity.Comment;

import java.util.List;
import java.util.UUID;

/**
 * @author Stsiapan Balashenka
 * @version 1.0
 */
public interface CommentService {
    List<Comment> getAllCommentsByOfferId(UUID offerId);
}
