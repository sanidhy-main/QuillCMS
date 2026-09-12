package com.sanidhy.quillcms.service;

import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(String name, String bio, String email, String mobile, int year, int month, int day) {
        User user = new User();
        user.setUsername(name);
        user.setBio(bio);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setDOB(year, month, day);

        userRepository.save(user);
    }

    public void editUsername(User user, String name) {
        //Verify if username is available, and follows rules
        user.setUsername(name);
        userRepository.save(user);
    }

    public void editBio(User user, String bio) {
        //Verify if bio follows the rules
        user.setBio(bio);
        userRepository.save(user);
    }

    public void editEmail(User user, String email) {
        //Verify if email follows the rules
        user.setEmail(email);
        //Verify with otp
        userRepository.save(user);
    }

    public void editMobile(User user, String mobile) {
        //Verify if mobile number follows the rules
        user.setMobile(mobile);
        //Verify with otp
        userRepository.save(user);
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