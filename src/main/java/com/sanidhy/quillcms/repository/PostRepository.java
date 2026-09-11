package com.sanidhy.quillcms.repository;

import com.sanidhy.quillcms.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    //save(post)
    //saveAll(posts)
    //saveAndFlush(post)
    //saveAllAndFlush(posts)

    //findById(id)
    //findAll()
    //findAllById(ids)
    //existsById(id)
    //count()

    //delete(post)
    //deleteById(id)
    //deleteAll()
    //deleteAll(posts)
    //deleteAllById(ids)
    //deleteAllInBatch(posts)
    //deleteAllByIdInBatch(ids)

    //flush()
    //getReferenceById(id)

    //Optional<Post> findByColumn(column)
}
