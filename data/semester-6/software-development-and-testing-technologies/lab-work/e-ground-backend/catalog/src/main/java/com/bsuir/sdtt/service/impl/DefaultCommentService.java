package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.entity.Comment;
import com.bsuir.sdtt.repository.CommentRepository;
import com.bsuir.sdtt.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Service
public class DefaultCommentService implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public DefaultCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAllCommentsByOfferId(UUID id) {
        return commentRepository.findAllByOfferId(id);
    }
}
