package application.grid.service;


import application.grid.domain.dto.request.UserDTORequest;
import application.grid.domain.dto.response.UserDTOResponse;
import application.grid.domain.entity.User;
import application.grid.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final LoggerService loggerService;

    private final PasswordEncoder passwordEncoder;


    // Buscar usuário por ID
    public UserDTOResponse findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(("Usuario de id %s não encontrado"))));
        return UserDTOResponse.fromEntity(user);
    }

    // Criar novo usuário
    public void createUser(UserDTORequest userDTO) {
        loggerService.infoWithBody("USER_CREATE", this.getClass(), "Criando novo usuário", userDTO);

        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        userRepository.save(user);
    }

    // Editar usuário
    public UserDTOResponse updateUser(UserDTORequest userDTO) {
        loggerService.infoWithBody("USER_UPDATE", this.getClass(), "Editando usuário", userDTO);
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new EntityNotFoundException(String.format(("Usuario de id %s não encontrado"))));

        user.setLogin(userDTO.getLogin());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setName(userDTO.getName());

        userRepository.save(user);
        return UserDTOResponse.fromEntity(user);

    }


    // Implementação do UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}