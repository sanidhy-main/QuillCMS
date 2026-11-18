package com.sanidhy.quillcms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDate;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.Set;
import java.util.HashSet;
import org.hibernate.proxy.HibernateProxy;
import java.util.Objects;
import com.sanidhy.quillcms.exception.UserNotFoundException;

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

    @Column(name = "hashed_password")
    private String hashedPassword;

    @ManyToMany
    @JoinTable(name = "follows", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "followed_id"))
    private Set<User> followingSet = new HashSet<>();

    public Set<User> getFollowingSet() {
        return this.followingSet;
    }

    @ManyToMany
    @JoinTable(name = "blocks", joinColumns = @JoinColumn(name = "blocker_id"), inverseJoinColumns = @JoinColumn(name = "blocked_id"))
    private Set<User> blockedSet = new HashSet<>();

    public Set<User> getBlockingSet() {
        return this.blockedSet;
    }

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

    public void setDOB(LocalDate dob) {
        this.dob = dob;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobileNumber = mobile;
    }

    public void setHashedPassword (String hashed) {
        this.hashedPassword = hashed;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClassWithoutInitializingProxy(this)
                != getClassWithoutInitializingProxy(obj)) {
            return false;
        }

        User user = (User) obj;

        return id != 0 && id == user.id;
    }

    @Override
    public int hashCode() {
        return getClassWithoutInitializingProxy(this).hashCode();
    }

    private static Class<?> getClassWithoutInitializingProxy(Object obj) {
        return obj instanceof HibernateProxy
                ? ((HibernateProxy) obj).getHibernateLazyInitializer().getPersistentClass()
                : obj.getClass();
    }
}
