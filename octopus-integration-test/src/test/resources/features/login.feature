Feature: Login

  Scenario: Trigger login as a new user
    Given I try to log in
    When TGR find request to path "/testdriver/performLogin"
    Then TGR current response body matches
      """
      .*Bad Request.*
      """
