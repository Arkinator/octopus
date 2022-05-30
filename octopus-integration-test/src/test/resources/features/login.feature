Feature: Login

  Scenario: Trigger login as a new user
    Given I control the healthendpoint
    When I try to log in
    And TGR find request to path "/testdriver/.*"
    And TGR current response at "$.responseCode" matches "200"
    And TGR current response at "$.body" matches "OK"
    And TGR current response at "$.sender.domain" matches "testdriver"
    And TGR find next request to path "/testdriver/.*"
    Then TGR print current response as rbel-tree
    And TGR current response at "$.body.status" matches "400"
    And TGR current response at "$.body.error" matches "Bad Request"
    And TGR current response at "$.body.path" matches "/login"
    And TGR current response at "$.header.Connection" matches "keep-alive"
    And TGR current response at "$.responseCode" matches "200"
    And TGR current response at "$.sender.domain" matches "testdriver"
