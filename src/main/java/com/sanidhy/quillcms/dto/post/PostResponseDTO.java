package com.sanidhy.quillcms.dto.post;

import com.sanidhy.quillcms.entity.User;

public class PostResponseDTO {
    private long id;
    private String body;
    private int likes;

    public long getId() {
            return this.id;
    }
    public String getBody() {
            return this.body;
    }

    public void setBody(String body) {
            this.body = body;
    }
    public int getLikes() {
            return likes;
    }
}
