package de.gematik.tuz.dojo.octopus.client;

import com.google.common.hash.Hashing;
import de.gematik.tuz.dojo.octopus.client.dto.NewUserDto;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("testdriver")
@Slf4j
public class TestDriverController {

    @Value("${services.identity}")
    private String identityServiceUrl;
    @Value("${services.shopping}")
    private String shoppingServiceUrl;
    private AtomicReference<String> userToken = new AtomicReference<>();

    @PostConstruct
    public void logServers() {
        log.info("id-service url: {}", identityServiceUrl);
        log.info("shopping-service url: {}", shoppingServiceUrl);
    }

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

    @GetMapping("deleteUser")
    public void deleteUser(@RequestParam("username") String username) {
        final HttpResponse response = Unirest.put(identityServiceUrl + "/deleteUser")
            .queryString("username", username)
            .asEmpty();
        if (!response.isSuccess()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failure while deleting user");
        }
    }

    @GetMapping("performLogin")
    public String performLogin(@RequestParam("username") String username, @RequestParam("password") String password) {
        log.info("Performing login with username '{}' and password '{}' at url '{}'",
            username, password, identityServiceUrl);
        return Unirest.post(identityServiceUrl + "/login")
            .queryString("passwordHash", Hashing.sha256().hashString(password).toString())
            .queryString("username", username)
            .asString()
            .ifSuccess(response -> userToken.set(response.getBody()))
            .getBody();
    }

    @GetMapping("trade")
    public String performTrade(
        @RequestParam("octopusName") String octopusName,
        @RequestParam("otherUserName") String otherUserName,
        @RequestParam("money") double money) {
        Long otherUserId = Long.parseLong(Unirest.get(identityServiceUrl + "/findUserIdToName")
            .queryString("username", otherUserName)
            .asString()
            .getBody());

        Unirest.post(shoppingServiceUrl + "/inventory/trade")
            .header("Authorization", "Bearer " + userToken.get())
            .queryString("octopusName", octopusName)
            .queryString("otherUserId", otherUserId)
            .queryString("moneyToGive", money)
            .asEmpty();

        return "{\"o\":\"k\"}";
    }

    @GetMapping("retrieveInventory")
    public String retrieveInventory() {
        return Unirest.get(shoppingServiceUrl + "/inventory")
            .header("Authorization", "Bearer " + userToken.get())
            .asString()
            .getBody();
    }

    @GetMapping("status")
    public String status() {
        return "OK";
    }
}
