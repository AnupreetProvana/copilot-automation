package com.provana.pages;

import com.provana.utils.ExcelUtil;
import com.provana.utils.ExcelUtil.Locator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Page-object for the SauceDemo login page.
 */
public final class LoginPage {

    /* ------------------------------------------------------------------ */
    /* driver / wait                                                      */
    /* ------------------------------------------------------------------ */
    private final WebDriver      driver;
    private final WebDriverWait  wait;

    /* ------------------------------------------------------------------ */
    /* locators – pulled once from Excel                                  */
    /* ------------------------------------------------------------------ */
    private static final Locator USER_FIELD   = ExcelUtil.loc("userField");
    private static final Locator PASS_FIELD   = ExcelUtil.loc("passField");
    private static final Locator LOGIN_BTN    = ExcelUtil.loc("loginBtn");
    private static final Locator ERROR_BANNER = ExcelUtil.loc("errorBanner");
    private static final Locator PRODUCTS_HDR = ExcelUtil.loc("productsHdr");

    /* ------------------------------------------------------------------ */
    /* ctor                                                               */
    /* ------------------------------------------------------------------ */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /* ------------------------------------------------------------------ */
    /* actions                                                            */
    /* ------------------------------------------------------------------ */
    public void open() {
        driver.get(ExcelUtil.cfg("baseUrl"));
    }

    public void login(String username, String password) {
        find(USER_FIELD).sendKeys(username);
        find(PASS_FIELD).sendKeys(password);
        find(LOGIN_BTN).click();
    }

    /* ------------------------------------------------------------------ */
    /* assertions / state helpers                                         */
    /* ------------------------------------------------------------------ */
    public boolean isOnProductsPage() {
        return wait.until(visible(PRODUCTS_HDR)).isDisplayed();
    }

    /** Exposes the banner text for negative-login assertions. */
    public String getErrorBannerText() {
        return wait.until(visible(ERROR_BANNER)).getText();
    }

    /* ------------------------------------------------------------------ */
    /* internal helpers                                                   */
    /* ------------------------------------------------------------------ */
    private WebElement find(Locator loc) {
        return driver.findElement(toBy(loc));
    }

    private Function<WebDriver, WebElement> visible(Locator loc) {
        return ExpectedConditions.visibilityOfElementLocated(toBy(loc));
    }

    private static By toBy(Locator l) {
        return switch (l.by()) {
            case "id"    -> By.id(l.value());
            case "name"  -> By.name(l.value());
            case "css"   -> By.cssSelector(l.value());
            case "xpath" -> By.xpath(l.value());
            default      -> throw new IllegalArgumentException("Unknown By: " + l.by());
        };
    }
}
