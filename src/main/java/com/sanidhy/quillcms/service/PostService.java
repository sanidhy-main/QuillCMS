package com.sanidhy.quillcms.service;

import org.springframework.stereotype.Service;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.entity.User;
import org.springframework.transaction.annotation.Transactional;
import com.sanidhy.quillcms.mapper.PostMapper;
import com.sanidhy.quillcms.dto.post.CreatePostDTO;
import com.sanidhy.quillcms.dto.post.PostResponseDTO;
import com.sanidhy.quillcms.dto.post.UpdatePostDTO;
import com.sanidhy.quillcms.exception.PostNotFoundException;
import com.sanidhy.quillcms.exception.AlreadyLikedException;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    public PostResponseDTO createPost(User user, CreatePostDTO dto) {
        Post post = postMapper.toEntity(dto);
        post.setUser(user);

        // Validate post content

        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDTO(savedPost);
    }

    public PostResponseDTO editPost(long id, UpdatePostDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        postMapper.updateEntity(post, dto);
        Post updatedPost = postRepository.save(post);
        return postMapper.toResponseDTO(updatedPost);
    }

    public void likePost(long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (post.getLikes().contains(user)) {
            throw new AlreadyLikedException("Post already liked");
        }

        post.getLikes().add(user);
        postRepository.save(post);
    }

    public void deletePost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        postRepository.delete(post);
    }
}
