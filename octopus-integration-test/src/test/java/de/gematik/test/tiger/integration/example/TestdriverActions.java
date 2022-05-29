package de.gematik.test.tiger.integration.example;

import io.cucumber.java.en.Given;
import net.serenitybdd.rest.SerenityRest;

public class TestdriverActions {

    @Given("I try to log in")
    public void iTryToLogIn() {
        SerenityRest.given()
            .queryParam("username", "foo")
            .queryParam("password", "secret")
            .get("http://localhost:5300/testdriver/performLogin");
    }
}
