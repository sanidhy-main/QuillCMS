package com.sanidhy.quillcms.repository;

import com.sanidhy.quillcms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //save(user)
    //saveAll(users)
    //saveAndFlush(user)
    //saveAllAndFlush(users)

    //findById(id)
    //findAll()
    //findAllById(ids)
    //existsById(id)
    //count()

    //delete(user)
    //deleteById(id)
    //deleteAll()
    //deleteAll(users)
    //deleteAllById(ids)
    //deleteAllInBatch(users)
    //deleteAllByIdInBatch(ids)

    //flush()
    //getReferenceById(id)

    //Optional<User> findByColumn(column)
}
