package com.sanidhy.quillcms.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "comments")
public class Comment {
    public Comment() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @Column(name = "body")
    private String body;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "comment_id")
    private Comment parentComment;

    @ManyToMany
    @JoinTable(name = "liked_comments", joinColumns = @JoinColumn(name = "comment_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likes = new HashSet<>();

    public long getCommentId() {
        return this.id;
    }

    public Long getParentCommentId() {
        return this.parentComment == null ? null : this.parentComment.getCommentId();
    }

    public User getUser() {
        return this.user;
    }

    public Post getPost() {
        return this.post;
    }

    public Long getUserId() {
        return this.user == null ? null : this.user.getId();
    }

    public Long getPostId() {
        return this.post == null ? null : this.post.getId();
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public Set<User> getLikes() {
        return likes;
    }
}
