package de.gematik.tuz.dojo.octopus.shopping;

import java.util.Optional;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class AuthorizationService {

    @Value("${services.identity}")
    private String identityServiceUrl;

    public long verifyAuthorizedAndReturnUserId(String authorizationHeader) {
        log.info("Checking for authorization with header '{}'", authorizationHeader);
        return Optional.ofNullable(authorizationHeader)
            .filter(header -> header.startsWith("Bearer "))
            .map(header -> header.substring("Bearer ".length()).trim())
            .map(bearerToken -> Unirest.get(identityServiceUrl + "/verifyIdentity?token="+bearerToken).asJson())
            .filter(HttpResponse::isSuccess)
            .map(result -> result.getBody().getObject().getLong("userId"))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not verify user identity"));
    }
}
