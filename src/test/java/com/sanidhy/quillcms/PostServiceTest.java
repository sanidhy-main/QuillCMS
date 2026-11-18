package com.sanidhy.quillcms;

import com.sanidhy.quillcms.dto.post.UpdatePostDTO;
import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.exception.AlreadyLikedException;
import com.sanidhy.quillcms.exception.NotLikedException;
import com.sanidhy.quillcms.exception.PostNotFoundException;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.repository.UserRepository;
import com.sanidhy.quillcms.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired UserRepository userRepository;

    @Test
    void createPostPersistsPostAndReturnsResponse() {
        User author = TestData.user(userRepository);
        var result = postService.createPost(author, TestData.createPostDTO("First post"));
        Post saved = postRepository.findById(result.getId()).orElseThrow();
        assertEquals("First post", result.getBody());
        assertEquals(author.getId(), saved.getUserId());
        assertEquals(0, result.getLikes());
    }

    @Test
    void getPostByIdReturnsPost() {
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Post body");
        var result = postService.getPostById(post.getId());
        assertEquals(post.getId(), result.getId());
        assertEquals("Post body", result.getBody());
        assertEquals(0, result.getLikes());
    }

    @Test
    void getPostByIdThrowsForMissingPost() {
        assertThrows(PostNotFoundException.class, () -> postService.getPostById(Long.MAX_VALUE));
    }

    @Test
    void editPostUpdatesBody() {
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Before");
        UpdatePostDTO dto = new UpdatePostDTO();
        dto.setBody("After");
        var result = postService.editPost(post.getId(), dto);
        assertEquals("After", result.getBody());
        assertEquals("After", postRepository.findById(post.getId()).orElseThrow().getBody());
    }

    @Test
    void editPostWithNullBodyLeavesBodyUnchanged() {
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Keep this");
        var result = postService.editPost(post.getId(), new UpdatePostDTO());
        assertEquals("Keep this", result.getBody());
    }

    @Test
    void editPostThrowsForMissingPost() {
        assertThrows(PostNotFoundException.class,
                () -> postService.editPost(Long.MAX_VALUE, new UpdatePostDTO()));
    }

    @Test
    void likePostAddsUserAndResponseReportsCount() {
        User liker = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Like me");
        postService.likePost(post.getId(), liker);
        assertTrue(postRepository.findById(post.getId()).orElseThrow().getLikes().contains(liker));
        assertEquals(1, postService.getPostById(post.getId()).getLikes());
    }

    @Test
    void likePostRejectsDuplicateLike() {
        User liker = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Like me");
        postService.likePost(post.getId(), liker);
        assertThrows(AlreadyLikedException.class, () -> postService.likePost(post.getId(), liker));
    }

    @Test
    void likePostThrowsForMissingPost() {
        assertThrows(PostNotFoundException.class,
                () -> postService.likePost(Long.MAX_VALUE, TestData.user(userRepository)));
    }

    @Test
    void unlikePostRemovesLike() {
        User liker = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Unlike me");
        postService.likePost(post.getId(), liker);
        postService.unlikePost(post.getId(), liker);
        assertFalse(postRepository.findById(post.getId()).orElseThrow().getLikes().contains(liker));
        assertEquals(0, postService.getPostById(post.getId()).getLikes());
    }

    @Test
    void unlikePostRejectsWhenNotLiked() {
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Not liked");
        assertThrows(NotLikedException.class,
                () -> postService.unlikePost(post.getId(), TestData.user(userRepository)));
    }

    @Test
    void unlikePostThrowsForMissingPost() {
        assertThrows(PostNotFoundException.class,
                () -> postService.unlikePost(Long.MAX_VALUE, TestData.user(userRepository)));
    }

    @Test
    void deletePostDeletesPostAndItsNestedComments() {
        User author = TestData.user(userRepository);
        User commenter = TestData.user(userRepository);
        Post post = TestData.post(postRepository, author, "Delete me");
        Comment root = TestData.comment(commentRepository, post, commenter, null, "Root");
        Comment reply = TestData.comment(commentRepository, post, commenter, root, "Reply");
        Comment nested = TestData.comment(commentRepository, post, commenter, reply, "Nested reply");

        postService.deletePost(post.getId());

        assertFalse(postRepository.existsById(post.getId()));
        assertFalse(commentRepository.existsById(root.getCommentId()));
        assertFalse(commentRepository.existsById(reply.getCommentId()));
        assertFalse(commentRepository.existsById(nested.getCommentId()));
    }

    @Test
    void deletePostClearsLikesBeforeDeleting() {
        User liker = TestData.user(userRepository);
        Post post = TestData.post(postRepository, TestData.user(userRepository), "Liked then deleted");
        postService.likePost(post.getId(), liker);
        assertDoesNotThrow(() -> postService.deletePost(post.getId()));
        assertFalse(postRepository.existsById(post.getId()));
    }

    @Test
    void deletePostThrowsForMissingPost() {
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(Long.MAX_VALUE));
    }
}
