package com.sanidhy.quillcms;

import com.sanidhy.quillcms.dto.user.UpdateUserDTO;
import com.sanidhy.quillcms.dto.user.CreateUserDTO;
import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.exception.AlreadyBlockedException;
import com.sanidhy.quillcms.exception.AlreadyFollowingException;
import com.sanidhy.quillcms.exception.EmailAlreadyExistsException;
import com.sanidhy.quillcms.exception.InvalidUserDataException;
import com.sanidhy.quillcms.exception.NotBlockedException;
import com.sanidhy.quillcms.exception.NotFollowingException;
import com.sanidhy.quillcms.exception.UserNotFoundException;
import com.sanidhy.quillcms.exception.UsernameAlreadyExistsException;
import com.sanidhy.quillcms.repository.UserRepository;
import com.sanidhy.quillcms.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;


    @Test
    void createUserRejectsDuplicateUsernameBeforeDatabaseInsert() {
        User existing = TestData.user(userRepository);
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername(existing.getUsername());
        dto.setEmail("another-" + existing.getEmail());
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUserRejectsDuplicateEmailBeforeDatabaseInsert() {
        User existing = TestData.user(userRepository);
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("new_" + existing.getUsername());
        dto.setEmail(existing.getEmail());
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUserPersistsUserAndReturnsResponse() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("registration_test_user");
        dto.setEmail("registration-test@example.test");
        dto.setBio("Registration test");
        dto.setMobile("9876543210");
        dto.setDOB(2000, 1, 1);
        // After adding password support, set the raw password here.
        var result = userService.createUser(dto);
        User saved = userRepository.findById(result.getId()).orElseThrow();
        assertEquals(dto.getUsername(), result.getUsername());
        assertEquals(dto.getEmail(), saved.getEmail());
    }

    @Test
    void getUserByIdReturnsMappedUser() {
        User user = TestData.user(userRepository);
        var result = userService.getUserById(user.getId());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getBio(), result.getBio());
        assertEquals(user.getDOB(), result.getDOB());
        assertEquals(user.getMobile(), result.getMobile());
    }

    @Test
    void getUserByIdThrowsForMissingUser() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(Long.MAX_VALUE));
    }

    @Test
    void updateUserChangesOnlyProvidedFields() {
        User user = TestData.user(userRepository);
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setBio("Updated bio");
        dto.setMobile("9876543210");

        var result = userService.updateUser(user.getId(), dto);
        assertEquals("Updated bio", result.getBio());
        assertEquals("9876543210", result.getMobile());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateUserThrowsForMissingUser() {
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(Long.MAX_VALUE, new UpdateUserDTO()));
    }

    @Test
    void updateUserRejectsUsernameUsedByAnotherUser() {
        TestData.user(userRepository);
        User target = TestData.user(userRepository);
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername(userRepository.findAll().get(0).getUsername());
        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.updateUser(target.getId(), dto));
    }

    @Test
    void updateUserRejectsEmailUsedByAnotherUser() {
        User first = TestData.user(userRepository);
        User target = TestData.user(userRepository);
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail(first.getEmail());
        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(target.getId(), dto));
    }

    @Test
    void updateUserAllowsKeepingOwnUsernameAndEmail() {
        User user = TestData.user(userRepository);
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        assertDoesNotThrow(() -> userService.updateUser(user.getId(), dto));
    }

    @Test
    void followUserAddsFollowingRelationship() {
        User follower = TestData.user(userRepository);
        User followed = TestData.user(userRepository);
        userService.followUser(follower, followed);
        assertTrue(userRepository.findById(follower.getId()).orElseThrow()
                .getFollowingSet().contains(followed));
    }

    @Test
    void followUserRejectsDuplicateFollow() {
        User follower = TestData.user(userRepository);
        User followed = TestData.user(userRepository);
        userService.followUser(follower, followed);
        assertThrows(AlreadyFollowingException.class,
                () -> userService.followUser(follower, followed));
    }

    @Test
    void followUserRejectsSelfFollow() {
        User user = TestData.user(userRepository);
        assertThrows(InvalidUserDataException.class, () -> userService.followUser(user, user));
    }

    @Test
    void unfollowUserRemovesFollowingRelationship() {
        User follower = TestData.user(userRepository);
        User followed = TestData.user(userRepository);
        userService.followUser(follower, followed);
        userService.unfollowUser(follower, followed);
        assertFalse(follower.getFollowingSet().contains(followed));
    }

    @Test
    void unfollowUserRejectsWhenNotFollowing() {
        User follower = TestData.user(userRepository);
        User followed = TestData.user(userRepository);
        assertThrows(NotFollowingException.class,
                () -> userService.unfollowUser(follower, followed));
    }

    @Test
    void blockUserAddsBlockAndRemovesFollowInBothDirections() {
        User blocker = TestData.user(userRepository);
        User blocked = TestData.user(userRepository);
        userService.followUser(blocker, blocked);
        userService.followUser(blocked, blocker);

        userService.blockUser(blocker, blocked);

        assertTrue(blocker.getBlockingSet().contains(blocked));
        assertFalse(blocker.getFollowingSet().contains(blocked));
        assertFalse(blocked.getFollowingSet().contains(blocker));
    }

    @Test
    void blockUserRejectsDuplicateBlock() {
        User blocker = TestData.user(userRepository);
        User blocked = TestData.user(userRepository);
        userService.blockUser(blocker, blocked);
        assertThrows(AlreadyBlockedException.class,
                () -> userService.blockUser(blocker, blocked));
    }

    @Test
    void blockUserRejectsSelfBlock() {
        User user = TestData.user(userRepository);
        assertThrows(InvalidUserDataException.class, () -> userService.blockUser(user, user));
    }

    @Test
    void unblockUserRemovesBlock() {
        User blocker = TestData.user(userRepository);
        User blocked = TestData.user(userRepository);
        userService.blockUser(blocker, blocked);
        userService.unblockUser(blocker, blocked);
        assertFalse(blocker.getBlockingSet().contains(blocked));
    }

    @Test
    void unblockUserRejectsWhenNotBlocking() {
        User blocker = TestData.user(userRepository);
        User blocked = TestData.user(userRepository);
        assertThrows(NotBlockedException.class,
                () -> userService.unblockUser(blocker, blocked));
    }

    @Test
    void deleteUserRejectsMissingUser() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(Long.MAX_VALUE));
    }
}
