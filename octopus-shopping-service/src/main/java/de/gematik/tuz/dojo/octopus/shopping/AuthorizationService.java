package de.gematik.tuz.dojo.octopus.shopping;

import java.util.Optional;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorizationService {

    @Value("${services.identity}")
    private String identityServiceUrl;

    public void verifyAuthorized(String authorizationHeader) {
        Optional.ofNullable(authorizationHeader)
            .filter(header -> header.startsWith("Bearer "))
            .map(header -> header.substring("Bearer ".length()).trim())
            .filter(bearerToken -> Unirest.put(identityServiceUrl + "/verify?token="+bearerToken)
                .asString().isSuccess())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not verify user identity"));
    }
}
