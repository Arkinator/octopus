package de.gematik.tuz.dojo.octopus.identity;

import lombok.RequiredArgsConstructor;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class UserController {

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

        return userRepository.addUser(userInformation);
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

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
