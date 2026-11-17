package com.sanidhy.quillcms.mapper;

import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.dto.comment.CreateCommentDTO;
import com.sanidhy.quillcms.dto.comment.UpdateCommentDTO;
import com.sanidhy.quillcms.dto.comment.CommentResponseDTO;

public class CommentMapper {
    public Comment toEntity(CreateCommentDTO dto) {
        Comment comment = new Comment();

        comment.setBody(dto.getBody());

        return comment;
    }

    public CommentResponseDTO toResponseDTO(Comment comment) {
        CommentResponseDTO response = new CommentResponseDTO();

        response.setBody(comment.getBody());
        response.setCommentId(comment.getCommentId());
        response.setLikes(comment.getLikes().size());

        return response;
    }

    public void updateEntity(Comment comment, UpdateCommentDTO dto) {
        if (dto.getBody() != null) {
            comment.setBody(dto.getBody());
        }
    }
}
