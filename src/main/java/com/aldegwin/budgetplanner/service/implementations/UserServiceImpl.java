package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.NotUniqueFieldException;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.UserRepository;
import com.aldegwin.budgetplanner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User save(User user) {
        Map<ErrorCode, String> errors = validateUniqueFields(user);
        if (!errors.isEmpty())
            throw new NotUniqueFieldException("Not unique fields", errors);
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new DatabaseEntityNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User update(User user) {
        User updatableUser = findById(user.getId());

        Map<ErrorCode, String> errors = validateUniqueFields(user);
        if (!errors.isEmpty())
            throw new NotUniqueFieldException("Not unique fileds" , errors);

        user.setPassword(updatableUser.getPassword());
        user.setLastLoginDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.delete(findById(id));
    }

    private Map<ErrorCode, String> validateUniqueFields(User user) {
        Map<ErrorCode, String> errors = new HashMap<>();

        userRepository.findUserByEmail(user.getEmail(), user.getId() != null ? user.getId() : -1)
                .ifPresent(existingUser ->
                        errors.put(ErrorCode.EMAIL_BUSY,
                                String.format("Email %s is already in use", user.getEmail())));

        userRepository.findUserByUsername(user.getUsername(), user.getId() != null ? user.getId() : -1)
                .ifPresent(existingUser ->
                        errors.put(ErrorCode.USERNAME_BUSY,
                                String.format("Username %s is already in use", user.getUsername())));

        return errors;
    }
}
