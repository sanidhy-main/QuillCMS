package com.sanidhy.quillcms.dto.user;

import java.time.LocalDate;

public class CreateUserDTO {
    private String username;
    private String bio;
    private LocalDate dob;
    private String email;
    private String mobileNumber;

    public void setUsername(String username) {
        this.username = username;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setDOB(int year, int month, int day) {
        this.dob = LocalDate.of(year, month, day);
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setMobile(String mobile) {
        this.mobileNumber = mobile;
    }

    public String getUsername() {
        return this.username;
    }
    public String getBio() {
        return this.bio;
    }
    public LocalDate getDOB() {
        return this.dob;
    }
    public String getEmail() {
        return this.email;
    }
    public String getMobile() {
        return this.mobileNumber;
    }
}
