Feature: Login via data/locators coming from Excel

  Background:
    # Loads URL, user/pass & locators behind the scenes.

  @positive
  Scenario: Successful login using valid credentials
    Given I am on the Sauce Demo login page
    When I login with excel credentials "validUser" and "validPass"
    Then I should see the Products page

  @negative @screenshot
  Scenario: Unsuccessful login using invalid credentials
    Given I am on the Sauce Demo login page
    When I login with excel credentials "invalidUser" and "invalidPass"
    Then I should see an error banner containing
      """Epic sadface"""

  @failure @screenshot
  Scenario: Intentional failure to test screenshot capture
    Given I am on the Sauce Demo login page
    When I login with excel credentials "validUser" and "validPass"
    Then I should see an error banner containing
      """This text does not exist"""
