package de.gematik.test.tiger.integration.example;

import io.cucumber.java.en.Given;
import net.serenitybdd.rest.SerenityRest;

public class TestdriverActions {

    @Given("I try to log in")
    public void iTryToLogIn() {
        SerenityRest.given()
            .queryParam("username", "foo")
            .queryParam("password", "secret")
            .get("http://testdriver/testdriver/performLogin");
    }

    @Given("I control the healthendpoint")
    public void iControlTheHealthEndpoint() {
        SerenityRest.given()
            .get("http://testdriver/testdriver/status");
    }
}
