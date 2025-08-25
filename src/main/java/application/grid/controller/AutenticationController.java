package application.grid.controller;


import application.grid.domain.dto.request.AutenticationDataDTO;
import application.grid.domain.dto.response.DataTokenJWT;
import application.grid.domain.entity.User;
import application.grid.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity login(@RequestBody @Valid AutenticationDataDTO data){
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.getUserName(), data.getPassword());
        var authentication = manager.authenticate(authenticationToken);
        var user = (User) authentication.getPrincipal();

        var tokenJWT = tokenService.generateToken(user);
        return ResponseEntity.ok(new DataTokenJWT(
                user.getId(),
                tokenJWT,
                user.getName()
        ));
    }
}