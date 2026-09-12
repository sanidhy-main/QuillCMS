package com.sanidhy.quillcms.service;

import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.entity.Post;

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

        commentRepository.save(comment);
    }

    //likeComment() -> Probably also needs to be a table (many to many)
}
