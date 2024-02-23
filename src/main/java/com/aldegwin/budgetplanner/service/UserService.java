package com.aldegwin.budgetplanner.service;

import com.aldegwin.budgetplanner.model.User;

public interface UserService {
    User save(User user);

    User findById(Long id);

    User update(User user);

    void deleteById(Long id);
}
