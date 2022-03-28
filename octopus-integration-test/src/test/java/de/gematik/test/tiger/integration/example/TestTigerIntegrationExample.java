/*
 * ${GEMATIK_COPYRIGHT_STATEMENT}
 */

package de.gematik.test.tiger.integration.example;

import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;

@Slf4j
public class TestTigerIntegrationExample {

  @When("I register new user with name {string} and password {string}")
  public void userRequestsStartpage(final String username, final String password) {
    SerenityRest.proxy("localhost", 9191);
    SerenityRest.get(
            "http://octopusClient/testdriver/performRegistration?"
                + "username="
                + username
                + "&password="
                + password)
        .asString();
  }

  @When("I login as user {string} with password {string}")
  public void iLoginAsUserWithPassword(String username, String password) {
    SerenityRest.proxy("localhost", 9191);
    SerenityRest.get(
            "http://octopusClient/testdriver/performLogin?"
                + "username="
                + username
                + "&password="
                + password)
        .asString();
  }
}
