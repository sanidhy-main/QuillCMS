package com.sanidhy.quillcms.dto.comment;

import com.sanidhy.quillcms.dto.user.UserResponseDTO;
import java.util.Set;
import java.util.HashSet;

public class CommentResponseDTO {
    private long id;
    private String body;
    private int likes;

    public long getCommentId() {
        return this.id;
    }
    public String getBody() {
        return this.body;
    }
    public int getLikes() {
        return likes;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
