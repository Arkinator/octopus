Feature: Test Tiger BDD

  Scenario: Register new user and log in
    Given TGR show blue banner "Registering user..."
    When I register new user with name "${octopus.testUser.name}" and password "${octopus.testUser.password}"
    Then TGR find request to path "/registerNewUser"
    Then TGR current response with attribute "$.body.username" matches "${octopus.testUser.name}"
#    Then TGR current response with attribute "$.body.password" matches "!{sha256('geheim123')}"

    Given TGR show blue banner "Log in user..."
    When I login as user "${octopus.testUser.name}" with password "${octopus.testUser.password}"
    Then TGR find request to path "/login"
    Then TGR current response with attribute "$.body.body.name" matches "${octopus.testUser.name}"

  Scenario: Log in with wrong password
    Given TGR show blue banner "Log in with wrong password..."
    When I login as user "${octopus.testUser.name}" with password "falsches passwort"
    Then TGR find next request to path "/login"
    Then TGR current response with attribute "$.responseCode" matches "400"
    Then TGR current response at "$.body" matches as "JSON"
    """
      {
        "timestamp":"${json-unit.ignore}",
        "status":400,
        "error":"Bad Request",
        "path":"/login"
      }
    """

  Scenario: Log in with wrong username
    Given TGR show blue banner "Log in with wrong username..."
    When I login as user "notAUser" with password "${octopus.testUser.password}"
    Then TGR find next request to path "/login"
    Then TGR current response with attribute "$.responseCode" matches "400"
    Then TGR current response at "$.body" matches as "JSON"
    """
      {
        "timestamp":"${json-unit.ignore}",
        "status":400,
        "error":"Bad Request",
        "path":"/login"
      }
    """

  Scenario: Delete user - login should no longer work
    Given TGR show blue banner "Deleting user and trying to log in..."
    And I delete user with name "${octopus.testUser.name}"
    When I login as user "${octopus.testUser.name}" with password "${octopus.testUser.password}"
    Then TGR find next request to path "/login"
    Then TGR current response with attribute "$.responseCode" matches "400"
    Then TGR current response at "$.body" matches as "JSON"
    """
      {
        "timestamp":"${json-unit.ignore}",
        "status":400,
        "error":"Bad Request",
        "path":"/login"
      }
    """
