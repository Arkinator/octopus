Feature: Show Inventory

  Scenario: Register new user and show inventory
    Given User "${octopus.user.name}" is not registered
    And I register as user "${octopus.user.name}" with password "${octopus.user.password}"
    When I try to log in as "${octopus.user.name}" with password "${octopus.user.password}"
    And I try to view my inventory
    Then TGR find last request to path "/testdriver/retrieveInventory"
    And TGR current response at "$.responseCode" matches "200"
    And TGR current response at "$.body.0.gender" matches "(MALE|FEMALE)"
    And TGR current response at "$.body.1.gender" matches "(MALE|FEMALE)"

  Scenario: Invalidate Token: Show inventory should be declined
    Given invalidate token on inventory-request
    When I try to view my inventory
    Then TGR find last request to path "/testdriver/retrieveInventory"
    And TGR current response at "$.body.status" matches "403"
    And TGR current response at "$.body.error" matches "Forbidden"
