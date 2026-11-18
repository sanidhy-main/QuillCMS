package com.sanidhy.quillcms.repository;

import com.sanidhy.quillcms.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_PostId(long postId);
    List<Comment> findByParentComment_Id(long parentCommentId);
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
