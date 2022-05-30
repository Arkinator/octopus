Feature: Login

  Scenario: Login as unregistered user, then register and finally login again
    Given User "user1" is not registered
    When I try to log in as "user1" with password "secret1"
    And TGR find request to path "/testdriver/performLogin"
    And TGR current response at "$.body.status" matches "400"

    Then I register as user "user1" with password "secret1"
    And TGR find last request to path "/testdriver/performRegistration"
    And TGR current response at "$.responseCode" matches "200"
    And TGR current response at "$.body.name" matches "user1"

    Then I try to log in as "user1" with password "secret1"
    And TGR find last request to path "/testdriver/performLogin"
    And TGR current response at "$.responseCode" matches "200"
