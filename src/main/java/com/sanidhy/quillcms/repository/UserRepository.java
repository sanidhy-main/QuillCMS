package com.sanidhy.quillcms.repository;

import com.sanidhy.quillcms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByFollowingSetContaining(User user);
    List<User> findByBlockedSetContaining(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
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
