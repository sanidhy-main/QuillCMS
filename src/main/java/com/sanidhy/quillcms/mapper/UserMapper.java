package com.sanidhy.quillcms.mapper;

import org.springframework.stereotype.Component;
import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.dto.user.CreateUserDTO;
import com.sanidhy.quillcms.dto.user.UpdateUserDTO;
import com.sanidhy.quillcms.dto.user.UserResponseDTO;

@Component
public class UserMapper {
    public User toEntity(CreateUserDTO dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setBio(dto.getBio());
        user.setDOB(dto.getDOB());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setBio(user.getBio());
        response.setDOB(user.getDOB());
        response.setEmail(user.getEmail());
        response.setMobile(user.getMobile());

        return response;
    }

    public void updateEntity(User user, UpdateUserDTO dto) {
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }

        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }

        if (dto.getDOB() != null) {
            user.setDOB(dto.getDOB());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getMobile() != null) {
            user.setMobile(dto.getMobile());
        }
    }
}
