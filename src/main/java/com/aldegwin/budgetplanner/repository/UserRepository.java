package com.aldegwin.budgetplanner.repository;

import com.aldegwin.budgetplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User WHERE username=:username and id!=:id")
    Optional<User> findUserByUsername(String username, Long id);
    @Query("FROM User WHERE email=:email and id!=:id")
    Optional<User> findUserByEmail(String email, Long id);
}
