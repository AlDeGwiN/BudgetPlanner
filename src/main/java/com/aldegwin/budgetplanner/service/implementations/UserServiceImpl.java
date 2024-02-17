package com.aldegwin.budgetplanner.service.implementations;

import com.aldegwin.budgetplanner.exception.DatabaseEntityNotFoundException;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.repository.UserRepository;
import com.aldegwin.budgetplanner.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
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
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
