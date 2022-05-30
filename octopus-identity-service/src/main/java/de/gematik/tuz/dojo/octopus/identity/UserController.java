package de.gematik.tuz.dojo.octopus.identity;

import de.gematik.octopussi.user.UserInformation;
import java.security.cert.X509Certificate;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.X509Util;
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

    X509Certificate certificate;

    {
        try {
            final X509Util x509Util = new X509Util();
            certificate = x509Util.fromBase64Der(
                "MIIC6jCCAdKgAwIBAgIGAYEUR+3QMA0GCSqGSIb3DQEBCwUAMDYxNDAyBgNVBAMM"
                    + "K01PMHRUbHJMR3FNblVJZzBIaEMzazY4RExEc2VzcHowTS1tQmxYUC0zLWswHhcN"
                    + "MjIwNTMwMDkyNDI3WhcNMjMwMzI2MDkyNDI3WjA2MTQwMgYDVQQDDCtNTzB0VGxy"
                    + "TEdxTW5VSWcwSGhDM2s2OERMRHNlc3B6ME0tbUJsWFAtMy1rMIIBIjANBgkqhkiG"
                    + "9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3omo8e7hqAn/DziC914E9W7o4uOnqKXydWav"
                    + "yc99SJoWmc6rIuwVatFQkHLeU6RATKt8oFc0/D/LSK96CuY63SGqYUlbtn9H2vdQ"
                    + "w3szJa8AYbiMq45Dr1ooELIojTP4o00dxa5fYc61+TY9LUzUsqjTYUHEzbgK5Ga3"
                    + "Td13/a0dsbGrrOF6NHLC5UA1nd+qgN7E5CF40CTR1HTVime7MoTunLsP6rK8VWXm"
                    + "jCSUf/Qnu8k8rNHUXTOWC15fTRHboInxa7nnYn7ZI+Da243kbP1etln70O2jshS0"
                    + "CJ8k3C6tvvNNtGZZrjvBJre3MRYowwBXcC4R1OUn2y8MY1XlfQIDAQABMA0GCSqG"
                    + "SIb3DQEBCwUAA4IBAQB7lxTYONY37sVGl6MSkLKs2DPcIojZxrsKmCC8FlxGGPJq"
                    + "AmAX0c61Zm0LaESEQIBCzvtMUi3sZf/uIw/E94udxoyo+opOJTsdyjLxwSf47KkA"
                    + "38uCCwEUqetSNe4K7yt5oQ34JiTnrCjL2LsmGfovdtSVPl0cSHtDQmDsYpY3QsT6"
                    + "wxfaruc6F9IlQOalQRxYjIsH+VdjmczesleYsGTpwCcJ0QjOC3pW+FZJj7HfJmDo"
                    + "A7PAqai4m1N/sCJ7QXru6z/j1MC3uuk1r6e4cStMNdX9t1SwOa8KaBxQMHcYJnt+"
                    + "RkK8/kcw9whVSqhFZlABD/Z0ASdcjiFh1SloDq9V");

            RSA_KEY = (RsaJsonWebKey) JsonWebKey.Factory.newJwk(
                "{\n"
                    + "    \"p\": \"-lmrOC6hCtzZEwZkScRbNkN3oFT5M5w3DX4OHjidsani9mEygRdyn-30_B9KYFZGWCp6sAP6wNh4bRYkwyilk1Lbc6X3c0FRwBXX2um3fLrQ6l7xopaftjhJMMP_c-AbArAh_shCR1evCnMNkSs0TADaBFOMF9_iWLBAFTUMEU0\",\n"
                    + "    \"kty\": \"RSA\",\n"
                    + "    \"q\": \"449Py0nW36nqu6JAmYwguspei_bXB_TE5qv3otJHyF4G9N51IceJvSN8Pm87eYIt6HqDdWgslZ0kzqLbZdOLDWpxFVU18ODwh9EfzxVaLCGzjKQKEIOw08ffbAii1K0FpS0mOEChrA0C9fFN0GGe5v8WIpknlgRpo7UqtPXSDPE\",\n"
                    + "    \"d\": \"ukXONZx4JFkht1LdRUPcsLgClhBrAV7OoXivwiQ8wS3Bp0L3uwaUMtI8Nn0AHlEJRWPxsKDuiAq6FQBVDpSCRyDr5f0qmIrX0bBcWRRhzrQ6hJGepecyPrAfg0ItcWxEPiTJu_NmgY8ugQjmkmmSTG3Gac1Md8oKy_4yhynPS3Uxscc66oxFqOMeKzDz5jWIQIK8gBrYQmrpiMTsCoM0Gv6gBSPCjiA8g7xkzqkzogsMmuaqcwWC1_vhZLrZij_NM_Lt3e-v2XAZPjwPGy-eCCM8Bsbv5HOB041EMYFJw56AS9IS6sL-7aUxxT7WrOUjpACEKzmiN_1uBGif6XZRQQ\",\n"
                    + "    \"e\": \"AQAB\",\n"
                    + "    \"use\": \"sig\",\n"
                    + "    \"qi\": \"r_aKk8QEaP4D2OQLipNCzDHckYBF3YloQVcFkqUjQrDmWT2SQYcEPj4RMK55BJXRYx8KvZ8D6Kv6J-QIIbr-hI5B93ARx01DTpQ3SLGZwQ4o_OaUIq-yBktjwXZssdSFBvn5Zhg7_qlJNlpQ36pIs6fQuxjO_GRtUlCTgRVf7wc\",\n"
                    + "    \"dp\": \"hmb9dPwTmTFXmM8lqDSygz9VSc5Uu1Byfdve2HqsrmT2ZC9qXcOo1hN6IqDp3S50NYEYvMZmKIIOpQHuWpfHzH__MOc5Ibc_nFAdwnkW-O-SmUC_mPokZD9zi6qtyhfWaGsG1THN418_qex6rCT1vpf2c7wmyep4KgT1Ym1IPUE\",\n"
                    + "    \"alg\": \"RS256\",\n"
                    + "    \"dq\": \"1g5pMQE0bxCXDgmCWu3Fm-_hICgB_inxTktVloPXolNQl-bqp-vbVV-b798SQyqBpL4aCscDqXk4tmfbmd10YBpQZZDSPNRbbmb5VQLw37KUAygLj51RKOZK9ITrJsPbG5Vs7l1dt0pGDrP-TpaFsyUg3UrUQfnEpYo1siPKgwE\",\n"
                    + "    \"n\": \"3omo8e7hqAn_DziC914E9W7o4uOnqKXydWavyc99SJoWmc6rIuwVatFQkHLeU6RATKt8oFc0_D_LSK96CuY63SGqYUlbtn9H2vdQw3szJa8AYbiMq45Dr1ooELIojTP4o00dxa5fYc61-TY9LUzUsqjTYUHEzbgK5Ga3Td13_a0dsbGrrOF6NHLC5UA1nd-qgN7E5CF40CTR1HTVime7MoTunLsP6rK8VWXmjCSUf_Qnu8k8rNHUXTOWC15fTRHboInxa7nnYn7ZI-Da243kbP1etln70O2jshS0CJ8k3C6tvvNNtGZZrjvBJre3MRYowwBXcC4R1OUn2y8MY1XlfQ\"\n"
                    + "}");
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
        jws.setCertificateChainHeaderValue(certificate);
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
