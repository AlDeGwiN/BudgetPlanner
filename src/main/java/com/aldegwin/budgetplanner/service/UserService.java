package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.User;

import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findById(Long id);

    Iterable<User> findAll();

    User update(User user);

    void delete(User user);

    void deleteById(Long id);
}
