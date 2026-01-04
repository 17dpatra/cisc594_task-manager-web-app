package io.taskmanager.authentication.domain.oauth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.List;

public record Client(String id,
                     String name,
                     boolean publicClient,
                     String secret,
                     List<String> redirectUris,
                     List<String> postLogoutRedirectUris,
                     List<String> scopes) {

    public RegisteredClient toRegisteredClient(PasswordEncoder encoder) {
        RegisteredClient.Builder b =
                RegisteredClient.withId(id)
                        .clientId(id)
                        .clientName(name)
                        .scopes(s -> s.addAll(scopes));

        if (publicClient) {
            b.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUris(u -> u.addAll(redirectUris))
                    .postLogoutRedirectUris(u -> u.addAll(postLogoutRedirectUris));
        } else {
            b.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientSecret(encoder.encode(secret))
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
        }
        return b.build();
    }
}