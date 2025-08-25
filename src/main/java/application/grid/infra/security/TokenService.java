package application.grid.infra.security;

import application.grid.domain.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class TokenService {


    @Value("${api.security.token.secret}") // Injeta o valor do secret definido nas configurações
    private String secret;

    // Gera um token JWT para o usuário autenticado
    public String generateToken(User user){
        try {
            var algorithm = Algorithm.HMAC256(secret); // Define o algoritmo HMAC256 com a chave secreta
            return JWT.create()
                    .withIssuer("API sge") // Define quem gerou o token
                    .withSubject(user.getLogin()) // Define o usuário como "dono" do token
                    .withClaim("id", user.getId().toString()) // Adiciona o ID do usuário no token
                    .withExpiresAt(dateExpires()) // Define o tempo de expiração do token
                    .sign(algorithm); // Assina o token com o algoritmo
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    private DecodedJWT decodeToken(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API sge")
                    .build()
                    .verify(tokenJWT);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
    }

    // Obtém o subject (usuário) a partir do token JWT
    public String getSubject(String tokenJWT){
        return decodeToken(tokenJWT).getSubject();
    }

    public String getHostId(String tokenJWT) {
        return decodeToken(tokenJWT).getClaim("hostId").asString();
    }

    public String getHostName(String tokenJWT) {
        return decodeToken(tokenJWT).getClaim("hostName").asString();
    }

    // Define o tempo de expiração do token (3 horas a partir do momento atual)
    private Instant dateExpires() {
        return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-02:00"));
    }
}
