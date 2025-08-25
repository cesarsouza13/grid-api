package application.grid.controller;


import application.grid.domain.dto.request.UserDTORequest;
import application.grid.domain.dto.response.UserDTOResponse;
import application.grid.infra.security.TokenService;
import application.grid.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gerenciamento de Usuarios")
public class UserController {

    private final UserService userService;

    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity create(@RequestBody @Valid UserDTORequest userDTO) {
        userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTOResponse> update(@RequestBody @Valid UserDTORequest userDTO) {
        UserDTOResponse updatedUser = userService.updateUser(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTOResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }





}
