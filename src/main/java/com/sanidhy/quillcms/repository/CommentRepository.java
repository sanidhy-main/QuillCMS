package com.sanidhy.quillcms.repository;

import com.sanidhy.quillcms.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
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
