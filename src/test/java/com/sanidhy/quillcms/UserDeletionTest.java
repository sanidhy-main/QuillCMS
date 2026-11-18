package com.sanidhy.quillcms;

import com.sanidhy.quillcms.entity.Comment;
import com.sanidhy.quillcms.entity.Post;
import com.sanidhy.quillcms.entity.User;
import com.sanidhy.quillcms.repository.CommentRepository;
import com.sanidhy.quillcms.repository.PostRepository;
import com.sanidhy.quillcms.repository.UserRepository;
import com.sanidhy.quillcms.service.CommentService;
import com.sanidhy.quillcms.service.PostService;
import com.sanidhy.quillcms.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDeletionTest {
    @Autowired UserService userService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;

    @Test
    void shouldDeleteUserAndCleanUpAllRelatedRecordsAndRelationships() {
        User target = TestData.user(userRepository);
        User follower = TestData.user(userRepository);
        User followedByTarget = TestData.user(userRepository);
        User blocker = TestData.user(userRepository);
        User blockedByTarget = TestData.user(userRepository);
        User liker = TestData.user(userRepository);
        User otherPostAuthor = TestData.user(userRepository);

        // Incoming and outgoing follow/block relationships.
        follower.getFollowingSet().add(target);
        target.getFollowingSet().add(followedByTarget);
        blocker.getBlockingSet().add(target);
        target.getBlockingSet().add(blockedByTarget);
        userRepository.saveAllAndFlush(java.util.List.of(
                target, follower, followedByTarget, blocker, blockedByTarget, liker, otherPostAuthor));

        // A post authored by the user being deleted, with comments and likes.
        Post targetPost = TestData.post(postRepository, target, "Target's post");
        postService.likePost(targetPost.getId(), liker);
        Comment targetPostRoot = TestData.comment(commentRepository, targetPost, liker, null, "Other user's comment");
        Comment targetPostReply = TestData.comment(commentRepository, targetPost, target, targetPostRoot, "Target's reply");
        commentService.likeComment(targetPostRoot.getCommentId(), target);

        // A comment authored by target on somebody else's post.
        Post otherPost = TestData.post(postRepository, otherPostAuthor, "Someone else's post");
        Comment targetComment = TestData.comment(commentRepository, otherPost, target, null, "Target's comment");
        Comment replyToTargetComment = TestData.comment(commentRepository, otherPost, liker,
                targetComment, "Reply to target's comment");
        Comment survivingComment = TestData.comment(commentRepository, otherPost, liker, null,
                "This comment should survive");
        commentService.likeComment(survivingComment.getCommentId(), target);

        long targetId = target.getId();
        long targetPostId = targetPost.getId();
        long otherPostId = otherPost.getId();
        long targetPostRootId = targetPostRoot.getCommentId();
        long targetPostReplyId = targetPostReply.getCommentId();
        long targetCommentId = targetComment.getCommentId();
        long replyToTargetCommentId = replyToTargetComment.getCommentId();
        long survivingCommentId = survivingComment.getCommentId();

        userService.deleteUser(targetId);

        assertFalse(userRepository.existsById(targetId));
        assertFalse(postRepository.existsById(targetPostId));
        assertFalse(commentRepository.existsById(targetPostRootId));
        assertFalse(commentRepository.existsById(targetPostReplyId));
        assertFalse(commentRepository.existsById(targetCommentId));
        assertFalse(commentRepository.existsById(replyToTargetCommentId));
        assertTrue(postRepository.existsById(otherPostId));

        assertFalse(userRepository.findById(follower.getId()).orElseThrow()
                .getFollowingSet().contains(target));
        assertFalse(userRepository.findById(followedByTarget.getId()).orElseThrow()
                .getFollowingSet().contains(target));
        assertFalse(userRepository.findById(blocker.getId()).orElseThrow()
                .getBlockingSet().contains(target));
        assertFalse(userRepository.findById(blockedByTarget.getId()).orElseThrow()
                .getBlockingSet().contains(target));

        // The other user's comment survives, but the deleted user's like is removed.
        Comment surviving = commentRepository.findById(survivingCommentId).orElseThrow();
        assertFalse(surviving.getLikes().contains(target));
    }

    @Test
    void shouldDeleteUserWithNoAssociatedContent() {
        User user = TestData.user(userRepository);
        long id = user.getId();
        userService.deleteUser(id);
        assertFalse(userRepository.existsById(id));
    }

    @Test
    void shouldDeleteUserWhoAuthoredNestedCommentsOnAnotherUsersPost() {
        User target = TestData.user(userRepository);
        User other = TestData.user(userRepository);
        Post post = TestData.post(postRepository, other, "Other post");
        Comment root = TestData.comment(commentRepository, post, target, null, "Target root");
        Comment child = TestData.comment(commentRepository, post, target, root, "Target child");
        Comment grandchild = TestData.comment(commentRepository, post, target, child, "Target grandchild");

        long rootId = root.getCommentId();
        long childId = child.getCommentId();
        long grandchildId = grandchild.getCommentId();
        userService.deleteUser(target.getId());

        assertFalse(commentRepository.existsById(rootId));
        assertFalse(commentRepository.existsById(childId));
        assertFalse(commentRepository.existsById(grandchildId));
        assertTrue(postRepository.existsById(post.getId()));
    }
}
