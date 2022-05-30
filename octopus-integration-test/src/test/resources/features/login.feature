Feature: Login

  Scenario: Trigger login as a new user
    Given I control the healthendpoint
    And I try to log in
    When TGR find request to path "/testdriver/.*"
    Then TGR current response body matches
      """
      OK
      """
    When TGR find next request to path "/testdriver/.*"
    Then TGR current response body matches
      """
      .*Bad Request.*
      """
