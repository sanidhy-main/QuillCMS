package com.sanidhy.quillcms;

import com.sanidhy.quillcms.dto.comment.UpdateCommentDTO;
import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.exception.AlreadyLikedException;
import com.sanidhy.quillcms.exception.CommentNotFoundException;
import com.sanidhy.quillcms.exception.NotLikedException;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.repository.UserRepository;
import com.sanidhy.quillcms.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired PostRepository postRepository;
    @Autowired UserRepository userRepository;

    @Test
    void commentOnPostPersistsCommentAndReturnsResponse() {
        User author = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Post");
        var result = commentService.commentOnPost(post, author, TestData.createCommentDTO("A comment"));
        Comment saved = commentRepository.findById(result.getCommentId()).orElseThrow();
        assertEquals("A comment", result.getBody());
        assertEquals(post.getId(), saved.getPostId());
        assertEquals(author.getId(), saved.getUserId());
        assertNull(saved.getParentCommentId());
        assertEquals(0, result.getLikes());
    }

    @Test
    void replyToCommentCreatesChildOnSamePost() {
        User author = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Post");
        Comment parent = TestData.comment(commentRepository, post, author, null, "Parent");
        var result = commentService.replyToComment(parent.getCommentId(), author,
                TestData.createCommentDTO("Child"));
        Comment reply = commentRepository.findById(result.getCommentId()).orElseThrow();
        assertEquals("Child", reply.getBody());
        assertEquals(parent.getCommentId(), reply.getParentCommentId());
        assertEquals(post.getId(), reply.getPostId());
    }

    @Test
    void replyToCommentThrowsForMissingParent() {
        assertThrows(CommentNotFoundException.class,
                () -> commentService.replyToComment(Long.MAX_VALUE,
                        TestData.user(userRepository), TestData.createCommentDTO("Reply")));
    }

    @Test
    void editCommentUpdatesBody() {
        Comment comment = createComment("Before");
        UpdateCommentDTO dto = new UpdateCommentDTO();
        dto.setBody("After");
        var result = commentService.editComment(comment.getCommentId(), dto);
        assertEquals("After", result.getBody());
        assertEquals("After", commentRepository.findById(comment.getCommentId()).orElseThrow().getBody());
    }

    @Test
    void editCommentWithNullBodyLeavesBodyUnchanged() {
        Comment comment = createComment("Keep this");
        var result = commentService.editComment(comment.getCommentId(), new UpdateCommentDTO());
        assertEquals("Keep this", result.getBody());
    }

    @Test
    void editCommentThrowsForMissingComment() {
        assertThrows(CommentNotFoundException.class,
                () -> commentService.editComment(Long.MAX_VALUE, new UpdateCommentDTO()));
    }

    @Test
    void likeCommentAddsUser() {
        User liker = TestData.user(userRepository);
        Comment comment = createComment("Like me");
        commentService.likeComment(comment.getCommentId(), liker);
        assertTrue(commentRepository.findById(comment.getCommentId()).orElseThrow()
                .getLikes().contains(liker));
    }

    @Test
    void likeCommentRejectsDuplicateLike() {
        User liker = TestData.user(userRepository);
        Comment comment = createComment("Like once");
        commentService.likeComment(comment.getCommentId(), liker);
        assertThrows(AlreadyLikedException.class,
                () -> commentService.likeComment(comment.getCommentId(), liker));
    }

    @Test
    void likeCommentThrowsForMissingComment() {
        assertThrows(CommentNotFoundException.class,
                () -> commentService.likeComment(Long.MAX_VALUE, TestData.user(userRepository)));
    }

    @Test
    void unlikeCommentRemovesLike() {
        User liker = TestData.user(userRepository);
        Comment comment = createComment("Unlike me");
        commentService.likeComment(comment.getCommentId(), liker);
        commentService.unlikeComment(comment.getCommentId(), liker);
        assertFalse(commentRepository.findById(comment.getCommentId()).orElseThrow()
                .getLikes().contains(liker));
    }

    @Test
    void unlikeCommentRejectsWhenNotLiked() {
        Comment comment = createComment("Not liked");
        assertThrows(NotLikedException.class,
                () -> commentService.unlikeComment(comment.getCommentId(), TestData.user(userRepository)));
    }

    @Test
    void unlikeCommentThrowsForMissingComment() {
        assertThrows(CommentNotFoundException.class,
                () -> commentService.unlikeComment(Long.MAX_VALUE, TestData.user(userRepository)));
    }

    @Test
    void deleteCommentDeletesItAndAllDescendants() {
        User author = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Post");
        Comment root = TestData.comment(commentRepository, post, author, null, "Root");
        Comment child = TestData.comment(commentRepository, post, author, root, "Child");
        Comment grandchild = TestData.comment(commentRepository, post, author, child, "Grandchild");

        commentService.deleteComment(root.getCommentId());

        assertFalse(commentRepository.existsById(root.getCommentId()));
        assertFalse(commentRepository.existsById(child.getCommentId()));
        assertFalse(commentRepository.existsById(grandchild.getCommentId()));
    }

    @Test
    void deleteCommentThrowsForMissingComment() {
        assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteComment(Long.MAX_VALUE));
    }

    private Comment createComment(String body) {
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Post for comment");
        return TestData.comment(commentRepository, post, TestData.user(userRepository), null, body);
    }
}
