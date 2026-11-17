package com.sanidhy.quillcms.service;

import org.springframework.stereotype.Service;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void createPost(User user, String desc) {
        Post post = new Post();
        //check if user is valid
        post.setUser(user);

        //check if it is valid
        post.setBody(desc);
        postRepository.save(post);
    }

    public void editPost(Post post, String desc) {
        //check if post exists
        //check if desc is valid
        post.setBody(desc);
        postRepository.save(post);
    }

    @Transactional
    public void likePost(Post post, User user) {
        if (!post.getLikes().contains(user)) {
            post.getLikes().add(user);
        }

        postRepository.save(post);
    }
}
