package io.taskmanager.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AuthenticationApplication {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AuthenticationApplication.class, args);
        ServerProperties serverProperties = context.getBean(ServerProperties.class);
        logger.info("Successfully started authentication application at port={}", serverProperties.getPort());
    }
}
