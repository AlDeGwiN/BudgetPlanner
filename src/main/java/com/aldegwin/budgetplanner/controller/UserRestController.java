package com.aldegwin.budgetplanner.controller;

import com.aldegwin.budgetplanner.communication.dto.UserDTO;
import com.aldegwin.budgetplanner.communication.request.UserRegistrationRequest;
import com.aldegwin.budgetplanner.communication.response.Response;
import com.aldegwin.budgetplanner.communication.response.error.Error;
import com.aldegwin.budgetplanner.communication.response.error.ErrorCode;
import com.aldegwin.budgetplanner.communication.response.error.ErrorResponse;
import com.aldegwin.budgetplanner.communication.response.success.SuccessResponse;
import com.aldegwin.budgetplanner.model.User;
import com.aldegwin.budgetplanner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Response> getUserById(@PathVariable("user_id") Long id) {
        UserDTO userDTO = getUserDTO(userService.findById(id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(SuccessResponse.builder()
                        .data(userDTO)
                        .build());
    }

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody @Valid UserRegistrationRequest req,
                                               UriComponentsBuilder uriComponentsBuilder) {
        User user = getUserFromRegistrationRequest(req);
        userService.save(user);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/users/{user_id}")
                        .build(Map.of("user_id", user.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(SuccessResponse.builder()
                        .data(getUserDTO(user))
                        .build());
    }


    @PutMapping("/{user_id}")
    public ResponseEntity<Response> updateUser(@PathVariable("user_id") Long id,
                                               @RequestBody @Valid UserDTO userDTO) {
        if(!Objects.equals(id, userDTO.getId()))
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ErrorResponse.builder()
                            .error(Error.builder()
                                    .errorCode(ErrorCode.VALIDATION_ERROR)
                                    .message("User ID in path does not match User ID in request body")
                                    .build())
                            .build());
        User user = userService.update(getUserFromDto(userDTO));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(SuccessResponse.builder()
                        .data(getUserDTO(user))
                        .build());
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("user_id") Long id) {
        userService.deleteById(id);

        String message = String.format("Resource /users/%d was deleted", id);
        return ResponseEntity.ok().body(Map.of("message", message));
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
