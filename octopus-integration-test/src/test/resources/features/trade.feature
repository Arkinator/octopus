Feature: Test Tiger BDD

  Background: Other user with given octopus
    Given I register new user with name "${octopus.user1.name}" and password "${octopus.user1.password}"
    And I register new user with name "${octopus.user2.name}" and password "${octopus.user2.password}"
    When I login as user "${octopus.user1.name}" with password "${octopus.user1.password}"
    Then I want to see my inventory
    And TGR find request to path "/inventory/generate"
    And TGR store current response node text value at "$.body.0.name" in variable "OCTOPUS_TO_TRADE"

  Scenario: Two Users trade one octopus
    Given I want to see my inventory
    And TGR find next request to path "/inventory"
    And TGR current response at "$.body.0.name" matches "${OCTOPUS_TO_TRADE}"

    When TGR show blue banner "Trading octopus from user1 to user2..."
    And I trade "${OCTOPUS_TO_TRADE}" to user "${octopus.user2.name}" for 100

    Then I want to see my inventory
    And TGR find next request to path "/inventory"
    #TODO ugly hack, we need find last request to path
    And TGR find next request to path "/inventory"
    And TGR current response at "$.body.0.name" matches "^((?!${OCTOPUS_TO_TRADE}).)*$"
