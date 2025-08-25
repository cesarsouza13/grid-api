package application.grid;

import application.grid.controller.AutenticationController;
import application.grid.infra.security.SecurityFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.token.TokenService;

@ExtendWith(MockitoExtension.class)
class GridApplicationTests {

	@Mock
	private TokenService tokenService;

	@Mock
	private SecurityFilter securityFilter;

	@InjectMocks
	private AutenticationController autenticationController;
	@Test
	void contextLoads() {
	}

}
