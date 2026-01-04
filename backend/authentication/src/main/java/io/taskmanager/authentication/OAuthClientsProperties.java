package io.taskmanager.authentication;

import io.taskmanager.authentication.domain.oauth.Client;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.oauth2")
public record OAuthClientsProperties(List<Client> clients) {
}
