package com.provana.stepdefinitions;

import com.provana.pages.LoginPage;
import com.provana.utils.DriverFactory;
import com.provana.utils.ExcelUtil;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

/**
 * Glue code for {@code login_excel.feature}.
 */
public class LoginExcelSteps {

    private final LoginPage page = new LoginPage(DriverFactory.getDriver());

    /* ---------------- Given ---------------- */
    @Given("I am on the Sauce Demo login page")
    public void i_am_on_the_login_page() {
        page.open();
    }

    /* ---------------- When ----------------- */
    @When("I login with excel credentials {string} and {string}")
    public void i_login_with_excel_credentials(String userKey, String passKey) {
        page.login(ExcelUtil.cfg(userKey), ExcelUtil.cfg(passKey));
    }

    /* ---------------- Then (positive) ------ */
    @Then("I should see the Products page")
    public void i_should_see_products() {
        Assertions.assertThat(page.isOnProductsPage())
                  .as("Verify Products header present")
                  .isTrue();
    }

    /* ---------------- Then (negative) ------ */
    @Then("I should see an error banner containing")
    public void i_should_see_error_banner(String expectedMessage) {
        String actual = page.getErrorBannerText().trim();
        Assertions.assertThat(actual)
                  .as("Verify error banner text")
                  .contains(expectedMessage.trim());
    }
}
