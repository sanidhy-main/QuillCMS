package com.sanidhy.quillcms.mapper;

import org.springframework.stereotype.Component;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.dto.post.CreatePostDTO;
import com.sanidhy.quillcms.dto.post.UpdatePostDTO;
import com.sanidhy.quillcms.dto.post.PostResponseDTO;

@Component
public class PostMapper {
    public Post toEntity(CreatePostDTO dto) {
        Post post = new Post();

        post.setBody(dto.getBody());

        return post;
    }

    public PostResponseDTO toResponseDTO(Post post) {
        PostResponseDTO response = new PostResponseDTO();

        response.setBody(post.getBody());
        response.setId(post.getId());
        response.setLikes(post.getLikes().size());

        return response;
    }

    public void updateEntity(Post post, UpdatePostDTO dto) {
        if (dto.getBody() != null) {
            post.setBody(dto.getBody());
        }
    }
}
