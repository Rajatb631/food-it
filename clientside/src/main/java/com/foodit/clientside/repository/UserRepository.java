package com.foodit.clientside.repository;

import com.foodit.clientside.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}