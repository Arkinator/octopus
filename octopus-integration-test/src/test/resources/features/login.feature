Feature: Login

  Scenario: Login as unregistered user, then register and finally login again
    Given User "${octopus.user.name}" is not registered
    When I try to log in as "${octopus.user.name}" with password "${octopus.user.password}"
    And TGR find request to path "/testdriver/performLogin"
    And TGR current response at "$.body" matches as JSON
      """
        {
          "timestamp": "${json-unit.ignore}",
          "status": 400,
          "error": "Bad Request",
          "path": "/login"
        }
      """

    Then I register as user "${octopus.user.name}" with password "${octopus.user.password}"
    And TGR find last request to path "/testdriver/performRegistration"
    And TGR current response at "$.responseCode" matches "200"
    And TGR current response at "$.body" matches as JSON
      """
        {
          "id": "${json-unit.ignore}",
          "name": "${octopus.user.name}"
        }
      """

    Then I try to log in as "${octopus.user.name}" with password "${octopus.user.password}"
    And TGR find last request to path "/testdriver/performLogin"
    And TGR current response at "$.responseCode" matches "200"
    And TGR print current response as rbel-tree

    And TGR current response at "$.body.body" matches as JSON
      """
        {
          "userId": "${json-unit.ignore}",
          "exp": "${json-unit.ignore}",
          "name": "${octopus.user.name}"
        }
      """
    And TGR current response at "$.body.header.alg" matches "RS256"
    And TGR current response at "$..name" matches "${octopus.user.name}"
    And TGR current response at "$.body.header.x5c.0.content.issuer" matches "${octopus.idService.certificate.dn}"
    And TGR current response at "$.body.header.x5c.0.content.subject" matches "${octopus.idService.certificate.dn}"
