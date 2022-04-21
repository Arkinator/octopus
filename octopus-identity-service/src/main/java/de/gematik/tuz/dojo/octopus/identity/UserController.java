package de.gematik.tuz.dojo.octopus.identity;

import de.gematik.octopussi.user.UserInformation;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Value("${services.shopping}")
    private String shoppingServiceUrl;
    private static RsaJsonWebKey RSA_KEY;

    static {
        try {
            System.out.println("GENERATING...");
            RSA_KEY = RsaJwkGenerator.generateJwk(2048);
            System.out.println("DONE GENERATING");
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
    }

    private final UserRepository userRepository;

    @PutMapping("registerNewUser")
    public UserInformation registerNewUser(@RequestBody UserInformation userInformation) {
        if (userRepository.findUserByName(userInformation.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        final UserInformation newUser = userRepository.addUser(userInformation);

        Unirest.post(shoppingServiceUrl + "/inventory/generate?id="+newUser.getId()).asString();

        return newUser;
    }

    @PutMapping("deleteUser")
    public void registerNewUser(@RequestParam("username") String username) {
        userRepository.deleteUser(username);
    }

    @PostMapping("login")
    public String login(
        @RequestParam("username") String username,
        @RequestParam("passwordHash") String passwordHash) throws JoseException {
        final UserInformation user = userRepository.findUserByName(username)
            .filter(u -> u.getPasswordHash().equals(passwordHash))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown user or invalid password"));

        JsonWebSignature jws = new JsonWebSignature();
        final JwtClaims jwtClaims = user.toClaims();
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(10);
        jws.setPayload(jwtClaims.toJson());
        jws.setKey(RSA_KEY.getPrivateKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        return jws.getCompactSerialization();
    }

    @GetMapping("verifyIdentity")
    public String verifyIdentity(@RequestParam("token") String tokenString) {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setAllowedClockSkewInSeconds(30)
            .setVerificationKey(RSA_KEY.getKey())
            .setJwsAlgorithmConstraints(
                ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
            .build();

        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(tokenString);
            log.info("JWT validation succeeded with claims {}", jwtClaims);
            return jwtClaims.getRawJson();
        } catch (InvalidJwtException e) {
            log.warn("Error while verifying user identity: ", e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User could not be verified!");
        }
    }

    @GetMapping("findUserIdToName")
    public Long findUserIdToName(@RequestParam("username") String username) {
        return userRepository.findUserByName(username)
            .map(UserInformation::getId)
            .orElse(-1L);
    }

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
