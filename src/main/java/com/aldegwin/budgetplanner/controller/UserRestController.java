package com.aldegwin.budgetplanner.controller;

import com.aldegwin.budgetplanner.communication.dto.UserDTO;
import com.aldegwin.budgetplanner.communication.request.UserRegistrationRequest;
import com.aldegwin.budgetplanner.exception.IdConflictException;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/{user_id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("user_id") Long id) {
        UserDTO userDTO = getUserDTO(userService.findById(id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserRegistrationRequest req,
                                               UriComponentsBuilder uriComponentsBuilder) {
        User user = getUserFromRegistrationRequest(req);
        UserDTO userDTO = getUserDTO(userService.save(user));
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/users/{user_id}")
                        .build(Map.of("user_id", user.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDTO);
    }


    @PutMapping("/{user_id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("user_id") Long id,
                                               @RequestBody @Valid UserDTO userDTO) {
        if(!Objects.equals(id, userDTO.getId()))
            throw new IdConflictException("User ID in path does not match User ID in request body");

        UserDTO updatedUserDTO = getUserDTO(userService.update(getUserFromDto(userDTO)));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUserDTO);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("user_id") Long id) {
        userService.deleteById(id);

        String message = String.format("Resource /users/%d was deleted", id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", message));
    }

    private User getUserFromRegistrationRequest(UserRegistrationRequest req) {
        return modelMapper.map(req, User.class);
    }

    private UserDTO getUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User getUserFromDto(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
