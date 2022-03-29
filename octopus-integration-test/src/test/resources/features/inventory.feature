Feature: Test Tiger BDD

  Scenario: Log in and show the inventory
    Given TGR show blue banner "Registering user..."
    And I register new user with name "${octopus.user1.name}" and password "${octopus.user1.password}"
    And TGR show blue banner "Log in user..."
    And I login as user "${octopus.user1.name}" with password "${octopus.user1.password}"

    When TGR show blue banner "Retrieving inventory..."
    Then I want to see my inventory

    Then TGR find request to path "/inventory"
    And TGR current response at "$.responseCode" matches "200"

  Scenario: Verification unsuccessful - should not show inventory
    Given next user validation is unsuccessful

    When TGR show blue banner "Retrieving inventory..."
    Then I want to see my inventory

    Then TGR find next request to path "/inventory"
    And TGR current response at "$.responseCode" matches "403"
