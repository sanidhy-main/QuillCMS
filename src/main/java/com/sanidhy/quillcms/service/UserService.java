package com.sanidhy.quillcms.service;

import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.sanidhy.quillcms.mapper.UserMapper;
import com.sanidhy.quillcms.dto.user.CreateUserDTO;
import com.sanidhy.quillcms.dto.user.UserResponseDTO;
import com.sanidhy.quillcms.exception.UsernameAlreadyExistsException;
import com.sanidhy.quillcms.exception.EmailAlreadyExistsException;
import com.sanidhy.quillcms.exception.UserNotFoundException;
import com.sanidhy.quillcms.dto.user.UpdateUserDTO;
import org.springframework.transaction.annotation.Transactional;
import com.sanidhy.quillcms.exception.AlreadyFollowingException;
import com.sanidhy.quillcms.exception.InvalidUserDataException;
import com.sanidhy.quillcms.exception.NotFollowingException;
import com.sanidhy.quillcms.exception.AlreadyBlockedException;
import com.sanidhy.quillcms.exception.NotBlockedException;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.repository.CommentRepository;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentService commentService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PostRepository postRepository, CommentRepository commentRepository, PostService postService, CommentService commentService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.commentService = commentService;
    }

    public UserResponseDTO createUser(CreateUserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Hash password before saving (implement during Security)

        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (dto.getUsername() != null &&
                !dto.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (dto.getEmail() != null &&
                !dto.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        userMapper.updateEntity(user, dto);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDTO(updatedUser);
    }

    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        List<Post> likedPosts = postRepository.findByLikesContaining(user);
        for (Post post : likedPosts) {
            post.getLikes().remove(user);
        }

        List<Comment> likedComments = commentRepository.findByLikesContaining(user);
        for (Comment comment : likedComments) {
            comment.getLikes().remove(user);
        }

        List<Comment> authoredComments = commentRepository.findByUser_Id(id);
        for (Comment comment : authoredComments) {
            commentService.deleteCommentTree(comment);
        }

        List<Post> authoredPosts = postRepository.findByUser_Id(id);
        for (Post post : authoredPosts) {
            postService.deletePost(post.getId());
        }

        List<User> followers = userRepository.findByFollowingSetContaining(user);
        for (User follower : followers) {
            follower.getFollowingSet().remove(user);
        }

        List<User> blockers = userRepository.findByBlockedSetContaining(user);
        for (User blocker : blockers) {
            blocker.getBlockingSet().remove(user);
        }

        user.getFollowingSet().clear();
        user.getBlockingSet().clear();

        userRepository.delete(user);
    }

    public void followUser(User follower, User following) {
        if (follower.equals(following)) {
            throw new InvalidUserDataException("Cannot follow yourself");
        }

        if (follower.getFollowingSet().contains(following)) {
            throw new AlreadyFollowingException("Already following this user");
        }

        follower.getFollowingSet().add(following);
        userRepository.save(follower);
    }

    public void unfollowUser(User follower, User following) {
        if (!follower.getFollowingSet().contains(following)) {
            throw new NotFollowingException("Not following this user");
        }

        follower.getFollowingSet().remove(following);
        userRepository.save(follower);
    }

    public void blockUser(User blocker, User blocking) {
        if (blocker.equals(blocking)) {
            throw new InvalidUserDataException("Cannot block yourself");
        }

        if (blocker.getBlockingSet().contains(blocking)) {
            throw new AlreadyBlockedException("Already blocking this user");
        }

        blocker.getBlockingSet().add(blocking);
        blocker.getFollowingSet().remove(blocking);
        blocking.getFollowingSet().remove(blocker);

        userRepository.save(blocker);
        userRepository.save(blocking);
    }

    public void unblockUser(User blocker, User blocking) {
        if (!blocker.getBlockingSet().contains(blocking)) {
            throw new NotBlockedException("Not blocking this user");
        }

        blocker.getBlockingSet().remove(blocking);
        userRepository.save(blocker);
    }
}