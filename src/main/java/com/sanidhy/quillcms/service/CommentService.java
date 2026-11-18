package com.sanidhy.quillcms.service;

import com.sanidhy.quillcms.exception.AlreadyLikedException;
import org.springframework.stereotype.Service;
import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.User;
import org.springframework.transaction.annotation.Transactional;
import com.sanidhy.quillcms.mapper.CommentMapper;
import com.sanidhy.quillcms.dto.comment.CreateCommentDTO;
import com.sanidhy.quillcms.dto.comment.CommentResponseDTO;
import com.sanidhy.quillcms.exception.CommentNotFoundException;
import com.sanidhy.quillcms.dto.comment.UpdateCommentDTO;
import java.util.List;
import com.sanidhy.quillcms.exception.NotLikedException;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentResponseDTO commentOnPost(Post post, User user, CreateCommentDTO dto) {
        Comment comment = commentMapper.toEntity(dto);
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponseDTO(savedComment);
    }

    public CommentResponseDTO replyToComment(long commentID, User user, CreateCommentDTO dto) {
        Comment parentComment = commentRepository.findById(commentID)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        Comment comment = commentMapper.toEntity(dto);
        comment.setParentComment(parentComment);
        comment.setPost(parentComment.getPost());
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toResponseDTO(savedComment);
    }

    public CommentResponseDTO editComment(long commentId, UpdateCommentDTO dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        commentMapper.updateEntity(comment, dto);

        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toResponseDTO(updatedComment);
    }

    public void likeComment(long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (comment.getLikes().contains(user)) {
            throw new AlreadyLikedException("Comment already liked");
        }

        comment.getLikes().add(user);
        commentRepository.save(comment);
    }

    public void unlikeComment(long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getLikes().contains(user)) {
            throw new NotLikedException("Comment not liked");
        }

        comment.getLikes().remove(user);
        commentRepository.save(comment);
    }

    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        deleteCommentTree(comment);
    }

    public void deleteCommentTree(Comment comment) {
        List<Comment> replies =
                commentRepository.findByParentComment_Id(comment.getCommentId());

        for (Comment reply : replies) {
            deleteCommentTree(reply);
        }

        comment.getLikes().clear();
        commentRepository.delete(comment);
    }
}
