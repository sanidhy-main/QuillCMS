package com.sanidhy.quillcms;

import com.sanidhy.quillcms.dto.comment.CreateCommentDTO;
import com.sanidhy.quillcms.dto.post.CreatePostDTO;
import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

final class TestData {
    private TestData() {}

    static User user(UserRepository repository) {
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        User user = new User();
        user.setUsername("user_" + token);
        user.setBio("Test bio");
        user.setDOB(LocalDate.of(2000, 1, 1));
        user.setEmail(token + "@example.test");
        user.setMobile("9" + token.substring(0, 9));
        user.setHashedPassword("$2a$10$test-only-not-a-real-password-hash");
        return repository.saveAndFlush(user);
    }

    static Post post(PostRepository repository, User author, String body) {
        Post post = new Post();
        post.setUser(author);
        post.setBody(body);
        return repository.saveAndFlush(post);
    }

    static Comment comment(CommentRepository repository, Post post, User author,
                           Comment parent, String body) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(author);
        comment.setParentComment(parent);
        comment.setBody(body);
        return repository.saveAndFlush(comment);
    }

    static CreatePostDTO createPostDTO(String body) {
        CreatePostDTO dto = new CreatePostDTO();
        dto.setBody(body);
        return dto;
    }

    static CreateCommentDTO createCommentDTO(String body) {
        CreateCommentDTO dto = new CreateCommentDTO();
        dto.setBody(body);
        return dto;
    }
}
