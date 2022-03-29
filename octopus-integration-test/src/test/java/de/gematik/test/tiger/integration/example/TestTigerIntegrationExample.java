/*
 * ${GEMATIK_COPYRIGHT_STATEMENT}
 */

package de.gematik.test.tiger.integration.example;

import de.gematik.rbellogger.modifier.RbelModificationDescription;
import de.gematik.test.tiger.common.config.TigerGlobalConfiguration;
import de.gematik.test.tiger.common.data.config.tigerProxy.TigerProxyConfiguration;
import de.gematik.test.tiger.proxy.client.TigerRemoteProxyClient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

@Slf4j
public class TestTigerIntegrationExample {

    private static final String USER_INVALIDATION_MODIFICATION = "userInvalidationModification";
    private static TigerRemoteProxyClient remoteProxyClient;

    @SneakyThrows
    @Before
    public void loadTestData() {
        log.info("Loading testdata...");
        TigerGlobalConfiguration.readFromYaml(
            FileUtils.readFileToString(new File("testData.yaml"), StandardCharsets.UTF_8),
            "octopus"
        );
        log.info("Done loading testdata!");
        if (remoteProxyClient == null) {
            remoteProxyClient = new TigerRemoteProxyClient(
                TigerGlobalConfiguration.resolvePlaceholders("http://localhost:${tiger.ports.proxyMgmt}"),
                TigerProxyConfiguration.builder().build());
        }
        remoteProxyClient.removeModification(USER_INVALIDATION_MODIFICATION);
    }

    @When("I register new user with name {string} and password {string}")
    public void iRegisterNewUserWithNameAndPassword(final String username, final String password) {
        SerenityRest.proxy("localhost", 9191);
        SerenityRest.get(
                "http://octopusClient/testdriver/performRegistration?"
                    + "username="
                    + TigerGlobalConfiguration.resolvePlaceholders(username)
                    + "&password="
                    + TigerGlobalConfiguration.resolvePlaceholders(password))
            .asString();
    }

    @When("I login as user {string} with password {string}")
    public void iLoginAsUserWithPassword(String username, String password) {
        SerenityRest.proxy("localhost", 9191);
        SerenityRest.get(
                "http://octopusClient/testdriver/performLogin?"
                    + "username="
                    + TigerGlobalConfiguration.resolvePlaceholders(username)
                    + "&password="
                    + TigerGlobalConfiguration.resolvePlaceholders(password))
            .asString();
    }

    @Then("I want to see my inventory")
    public void iWantToSeeMyInventory() {
        SerenityRest.proxy("localhost", 9191);
        SerenityRest.get("http://octopusClient/testdriver/retrieveInventory")
            .asString();
    }

    @Given("next user validation is unsuccessful")
    public void nextUserValidationIsUnsuccessful() {
        remoteProxyClient.addModificaton(
            RbelModificationDescription.builder()
                .name(USER_INVALIDATION_MODIFICATION)
                .condition("isRequest && request.url =^ \"/inventory\"")
                .targetElement("$.header.Authorization")
                .replaceWith("Bearer wrong token :D")
                .build()
        );
    }
}
