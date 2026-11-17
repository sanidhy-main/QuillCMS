package com.sanidhy.quillcms.service;

import org.springframework.stereotype.Service;
import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void commentOnPost(Post post, String body) {
        Comment comment = new Comment();
        comment.setBody(body);
        comment.setPost(post);

        commentRepository.save(comment);
    }

    public void replyToComment(long commentID, String body) {
        Comment comment = new Comment();
        comment.setBody(body);

        Comment parentComment = commentRepository.findById(commentID).orElseThrow();
        comment.setParentComment(parentComment);
        comment.setPost(parentComment.getPost());

        commentRepository.save(comment);
    }

    @Transactional
    public void likeComment(Comment comment, User user) {
        if (!comment.getLikes().contains(user)) {
            comment.getLikes().add(user);
        }

        commentRepository.save(comment);
    }
}
