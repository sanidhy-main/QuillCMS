package com.sanidhy.quillcms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.sanidhy.quillcms.entity.User;
import java.time.LocalDate;
import com.sanidhy.quillcms.service.UserService;
import com.sanidhy.quillcms.repository.UserRepository;
import com.sanidhy.quillcms.dto.user.CreateUserDTO;
import com.sanidhy.quillcms.dto.user.UserResponseDTO;

@SpringBootTest
@Transactional
class UserDeletionTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldDeleteUser() {
        User user = new User();
        user.setUsername("san.ch.07");
        user.setBio("test");
        user.setMobile("7302333265");
        user.setEmail("test@gmail.com");
        user.setDOB(LocalDate.of(2007, 7, 7));
        user.setHashedPassword("dummy-test-hash");

        User savedUser = userRepository.save(user);
        long userId = savedUser.getId();

        userService.deleteUser(userId);

        assertFalse(userRepository.existsById(userId));
    }
}
