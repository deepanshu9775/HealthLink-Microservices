package com.healthlink.healthlink_ai.repository;

import com.healthlink.healthlink_ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}