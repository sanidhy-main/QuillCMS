package com.sanidhy.quillcms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "bio")
    private String bio;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;

    public User() {
        isAdmin = false;
    }

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

    public long getId() {
        return this.id;
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

    public boolean getIsAdmin() {
        return this.isAdmin;
    }
}
