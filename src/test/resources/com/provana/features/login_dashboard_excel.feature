Feature: Login and Dashboard (Excel-driven, with advanced waits)
@demo
  Scenario: Login and verify dashboard with Excel credentials and locators
    Given I am on the IPACS login page
    When I login with Excel-driven credentials
    Then I should see the dashboard home navigation highlighted
    And the dashboard page title should match the expected value from Excel