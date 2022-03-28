Feature: Test Tiger BDD

  Scenario: Register new user and log in
    Given TGR show blue banner "Registering user..."
    When I register new user with name "testUser" and password "geheim123"
    Then TGR find request to path "/registerNewUser"
    Then TGR current response with attribute "$.body.username" matches "testUser"

    Given TGR show blue banner "Log in user..."
    When I login as user "testUser" with password "geheim123"
    Then TGR find request to path "/login"
    Then TGR current response with attribute "$.body.body.name" matches "testUser"

  Scenario: Log in with wrong password
    Given TGR show blue banner "Log in with wrong password..."
    When I login as user "testUser" with password "falsches passwort"
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
    When I login as user "notAUser" with password "geheim123"
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
