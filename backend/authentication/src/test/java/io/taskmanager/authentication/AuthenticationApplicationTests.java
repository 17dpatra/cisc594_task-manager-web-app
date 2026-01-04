package io.taskmanager.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class AuthenticationApplicationTests {

	@Test
	void contextLoads() {
        try(var mockedSpring = mockStatic(SpringApplication.class)) {
            var args = new String[] { "test" };
            AuthenticationApplication.main(args);
            mockedSpring.verify(() -> SpringApplication.run(AuthenticationApplication.class, new String[] { "test" }));
        }
	}

}
