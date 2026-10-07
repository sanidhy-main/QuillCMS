package com.sanidhy.quillcms.dto.post;

import com.sanidhy.quillcms.entity.User;

public class PostResponseDTO {
    private long id;
    private String body;
    private Set<User> likes = new HashSet<>();

    public long getId() {
            return this.id;
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
    public Set<User> getLikes() {
            return likes;
    }
}
