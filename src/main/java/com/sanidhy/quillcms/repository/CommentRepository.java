package com.sanidhy.quillcms.repository;

import com.sanidhy.quillcms.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.sanidhy.quillcms.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_Id(long id);
    List<Comment> findByParentComment_Id(long parentCommentId);
    List<Comment> findByUser_Id(long userId);
    List<Comment> findByLikesContaining(User user);
    //save(comment)
    //saveAll(comments)
    //saveAndFlush(comment)
    //saveAllAndFlush(comments)

    //findById(id)
    //findAll()
    //findAllById(ids)
    //existsById(id)
    //count()

    //delete(comment)
    //deleteById(id)
    //deleteAll()
    //deleteAll(comments)
    //deleteAllById(ids)
    //deleteAllInBatch(comments)
    //deleteAllByIdInBatch(ids)

    //flush()
    //getReferenceById(id)

    //Optional<Comment> findByColumn(column)
}
