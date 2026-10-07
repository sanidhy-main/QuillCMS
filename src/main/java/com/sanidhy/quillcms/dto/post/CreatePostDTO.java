package com.sanidhy.quillcms.dto.post;

import com.sanidhy.quillcms.entity.User;

public class CreatePostDTO {
    private String body;

    public long getUserId() {
        return this.getUser().getId();
    }
    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
            this.body = body;
        }
}
