package de.gematik.tuz.dojo.octopus.client;

import com.google.common.hash.Hashing;
import de.gematik.tuz.dojo.octopus.client.dto.NewUserDto;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("testdriver")
@Slf4j
public class TestDriverController {

    @Value("${services.identity}")
    private String identityServiceUrl;

    @GetMapping("performRegistration")
    public NewUserDto performRegistration(
        @RequestParam("username") String username, @RequestParam("password") String password) {
        final JSONObject newUserResult =
            Unirest.put(identityServiceUrl + "/registerNewUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(
                    new JSONObject(
                        Map.of(
                            "username",
                            username,
                            "passwordHash",
                            Hashing.sha256().hashString(password).toString())))
                .asJson()
                .mapBody(JsonNode::getObject);

        log.info("got result {}", newUserResult.toString());

        return NewUserDto.builder().id(newUserResult.getLong("id")).name(username).build();
    }

    @GetMapping("performLogin")
    public String performLogin(
        @RequestParam("username") String username, @RequestParam("password") String password) {
        return Unirest.post(identityServiceUrl + "/login")
            .queryString("passwordHash",Hashing.sha256().hashString(password).toString())
            .queryString("username", username)
            .asString()
            .getBody();
    }

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
