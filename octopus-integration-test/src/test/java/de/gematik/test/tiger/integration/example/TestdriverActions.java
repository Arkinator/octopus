package de.gematik.test.tiger.integration.example;

import de.gematik.rbellogger.modifier.RbelModificationDescription;
import de.gematik.test.tiger.common.config.TigerGlobalConfiguration;
import de.gematik.test.tiger.proxy.client.TigerRemoteProxyClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.nio.charset.StandardCharsets;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.web.util.UriUtils;

public class TestdriverActions {

    @Given("I control the healthendpoint")
    public void iControlTheHealthEndpoint() {
        SerenityRest.given()
            .get("http://testdriver/testdriver/status");
    }

    @When("I try to log in as {string} with password {string}")
    public void iTryToLogInAsWithPassword(String username, String password) {
        SerenityRest.given()
            .queryParam("username", resolveAndEncodeQueryParam(username))
            .queryParam("password", resolveAndEncodeQueryParam(password))
            .get("http://testdriver/testdriver/performLogin");
    }

    @Given("User {string} is not registered")
    public void userIsNotRegistered(String username) {
        SerenityRest.given()
            .queryParam("username", resolveAndEncodeQueryParam(username))
            .get("http://testdriver/testdriver/deleteUser");
    }

    @Then("I register as user {string} with password {string}")
    public void iRegisterAsUserWithPassword(String username, String password) {
        SerenityRest.given()
            .queryParam("username", resolveAndEncodeQueryParam(username))
            .queryParam("password", resolveAndEncodeQueryParam(password))
            .get("http://testdriver/testdriver/performRegistration");
    }

    @And("I try to view my inventory")
    public void iTryToViewMyInventory() {
        SerenityRest.given()
            .get("http://testdriver/testdriver/retrieveInventory");
    }

    private static String resolveAndEncodeQueryParam(String parameter) {
        return UriUtils.encodeQueryParam(
            TigerGlobalConfiguration.resolvePlaceholders(parameter),
            StandardCharsets.UTF_8);
    }

    @And("invalidate token on inventory-request")
    public void invalidateNextPassword() {
        try (final TigerRemoteProxyClient remoteProxyClient
            = new TigerRemoteProxyClient(TigerGlobalConfiguration.resolvePlaceholders(
                "http://localhost:${tiger.ports.adminPort}"))) {

            remoteProxyClient.addModificaton(RbelModificationDescription.builder()
                .condition("request.url =~ '/inventory.*' && isRequest")
                .targetElement("$.header.Authorization")
                .replaceWith("Bearer eyJhbGthis.tokenis.wrong")
                .build());
        }
    }
}
