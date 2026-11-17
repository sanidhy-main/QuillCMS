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

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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

    public void followUser(User follower, User following) {
        follower.getFollowingSet().add(following);
        userRepository.save(follower);
    }

    public void blockUser(User blocker, User blocking) {
        blocker.getBlockingSet().add(blocking);
        userRepository.save(blocker);
    }
}