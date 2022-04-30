Feature: the web application works
  Scenario: user makes call to GET /login
    Given the user is on the "/login"
    And with user account with username of "Cucumber" and password "@Ren1474"
    When the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    Then the user is on page with title "Homepage"

  Scenario: user makes call to GET /login with invalid credentials
    Given the user is on the "/login"
    When the user has "username" of "Cucumber2"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    Then the user is on page with title "Please sign in"

  Scenario: user makes call to GET college of engineering
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "coe"
    Then the user is on the external page "https://engineering.uiowa.edu/"

  Scenario: user makes call to GET office of sustainability
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "sus"
    Then the user is on the external page "https://sustainability.uiowa.edu/"

  Scenario: user makes call to GET university of iowa
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "uiowa"
    Then the user is on the external page "https://uiowa.edu/"

  Scenario: user makes call to GET /logs
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "logs"
    Then the user is on page with title "Logs"

  Scenario: user makes call to GET /view/sensors
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "view-sensors"
    Then the user is on page with title "View Sensors"

  Scenario: user makes call to GET /find/sensor
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "find-sensor"
    Then the user is on page with title "Find Sensor"

  Scenario: user makes call to GET /add/sensor
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "add-sensor"
    Then the user is on page with title "Add Sensor"

  Scenario: user makes call to GET /user/change_password
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "change-password"
    Then the user is on page with title "Change Password"

  Scenario: user makes call to GET /addUser
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "add-user"
    Then the user is on page with title "SignUp"

  Scenario: user makes call to GET /user/update
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "update-users"
    Then the user is on page with title "Update Users"

  Scenario: user makes call to GET /logs to view logs
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/logs"
    When the user clicks button "back" on page "logs"
    Then the user is on page with title "Homepage"


  Scenario: user makes call to GET /logout
    Given the user is on the "/login"
    And the user has "username" of "Cucumber"
    And the user has "password" of "@Ren1474"
    And the user clicks "submit" with class "btn"
    And the user is on the "/homepage"
    When the user clicks link "logout"
    Then the user is on page with title "Confirm Log Out?"