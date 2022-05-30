package de.gematik.test.tiger.integration.example;

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
            .queryParam("username", encodeQueryParam(username))
            .queryParam("password", encodeQueryParam(password))
            .get("http://testdriver/testdriver/performLogin");
    }

    @Given("User {string} is not registered")
    public void userIsNotRegistered(String username) {
        SerenityRest.given()
            .queryParam("username", encodeQueryParam(username))
            .get("http://testdriver/testdriver/deleteUser");
    }

    @Then("I register as user {string} with password {string}")
    public void iRegisterAsUserWithPassword(String username, String password) {
        SerenityRest.given()
            .queryParam("username", encodeQueryParam(username))
            .queryParam("password", encodeQueryParam(password))
            .get("http://testdriver/testdriver/performRegistration");
    }

    private static String encodeQueryParam(String parameter) {
        return UriUtils.encodeQueryParam(parameter, StandardCharsets.UTF_8);
    }
}
