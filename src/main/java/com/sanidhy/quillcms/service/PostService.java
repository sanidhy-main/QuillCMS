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
import com.sanidhy.quillcms.exception.NotLikedException;
import java.util.List;
import com.sanidhy.quillcms.entity.Comment;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
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

    public void unlikePost(long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (!post.getLikes().contains(user)) {
            throw new NotLikedException("Post not liked");
        }

        post.getLikes().remove(user);
        postRepository.save(post);
    }

    public PostResponseDTO getPostById(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        return postMapper.toResponseDTO(post);
    }

    public void deletePost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        List<Comment> comments = commentRepository.findByPost_PostId(id);

        for (Comment comment : comments) {
            if (comment.getParentCommentId() == null) {
                commentService.deleteCommentTree(comment);
            }
        }

        post.getLikes().clear();

        postRepository.delete(post);
    }
}
