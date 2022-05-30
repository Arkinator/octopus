Feature: Login

  Scenario: Login as unregistered user, then register and finally login again
    Given User "${octopus.user.name}" is not registered
    When I try to log in as "${octopus.user.name}" with password "${octopus.user.password}"
    And TGR find request to path "/testdriver/performLogin"
    And TGR current response at "$.body.status" matches "400"

    Then I register as user "${octopus.user.name}" with password "${octopus.user.password}"
    And TGR find last request to path "/testdriver/performRegistration"
    And TGR current response at "$.responseCode" matches "200"
    And TGR current response at "$.body.name" matches "${octopus.user.name}"

    Then I try to log in as "${octopus.user.name}" with password "${octopus.user.password}"
    And TGR find last request to path "/testdriver/performLogin"
    And TGR current response at "$.responseCode" matches "200"
    And TGR print current response as rbel-tree
    And TGR current response at "$.body.body.name" matches "${octopus.user.name}"
    And TGR current response at "$.body.header.alg" matches "RS256"
    And TGR current response at "$..name" matches "${octopus.user.name}"
    And TGR current response at "$.body.header.x5c.0.content.issuer" matches "${octopus.idService.certificate.dn}"
    And TGR current response at "$.body.header.x5c.0.content.subject" matches "${octopus.idService.certificate.dn}"
