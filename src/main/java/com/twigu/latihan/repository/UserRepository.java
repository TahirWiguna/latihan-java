package com.twigu.latihan.repository;

import com.twigu.latihan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findFirstByToken(String token);



}
