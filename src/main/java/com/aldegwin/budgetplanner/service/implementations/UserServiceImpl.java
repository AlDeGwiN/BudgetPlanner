package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.exception.NotUniqueFieldException;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.UserRepository;
import com.aldegwin.budgetplanner.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        validateUniqueFields(user);

        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isEmpty())
            throw new DatabaseEntityNotFoundException("User not found");

        return optionalUser.get();
    }

    @Override
    public User update(User user) {
        User updatableUser = findById(user.getId());

        validateUniqueFields(user);

        user.setPassword(updatableUser.getPassword());
        user.setLastLoginDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        findById(id);

        userRepository.deleteById(id);
    }

    private void validateUniqueFields(User user) {
        List<String> errorMessages = new ArrayList<>();

        userRepository.findUserByEmail(user.getEmail(), user.getId() != null ? user.getId() : -1)
                .ifPresent(existingUser ->
                        errorMessages.add(String.format("Email %s is already in use", user.getEmail())));

        userRepository.findUserByUsername(user.getUsername(), user.getId() != null ? user.getId() : -1)
                .ifPresent(existingUser ->
                        errorMessages.add(String.format("Username %s is already in use", user.getUsername())));

        if (!errorMessages.isEmpty()) {
            throw new NotUniqueFieldException(errorMessages);
        }
    }
}
