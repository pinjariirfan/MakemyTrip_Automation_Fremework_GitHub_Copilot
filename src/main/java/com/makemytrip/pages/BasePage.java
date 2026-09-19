package com.makemytrip.pages;

import com.makemytrip.config.ConfigReader;
import com.makemytrip.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Logger logger;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.logger = LogManager.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
    }

    public void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        driver.get(url);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            logger.info("Clicked on element located by: {}", locator);
        } catch (Exception e) {
            logger.warn("Standard click failed for {}. Attempting JavaScript click. Reason: {}", locator, e.getMessage());
            clickUsingJS(locator);
        }
    }

    public void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            logger.info("Clicked on WebElement: {}", element);
        } catch (Exception e) {
            logger.warn("Standard click failed on WebElement. Attempting JavaScript click. Reason: {}", e.getMessage());
            clickUsingJS(element);
        }
    }

    public void clickUsingJS(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        clickUsingJS(element);
    }

    public void clickUsingJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        logger.info("Executed JavaScript click on element: {}", element);
    }

    public void sendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        logger.info("Typed text '{}' into element: {}", text, locator);
    }

    public void sendKeys(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        logger.info("Typed text '{}' into element: {}", text, element);
    }

    public String getText(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return element.getText().trim();
    }

    public String getText(WebElement element) {
        return element.getText().trim();
    }

    public boolean isElementDisplayed(By locator, int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        scrollToElement(element);
    }

    /**
     * Opens a new browser tab within the same browser session and switches to it.
     */
    public String openNewTabAndSwitch(String url) {
        logger.info("Opening new browser tab and navigating to: {}", url);
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(url);
        return driver.getWindowHandle();
    }

    /**
     * Switches to a window/tab by handle.
     */
    public void switchToWindow(String windowHandle) {
        logger.info("Switching to window handle: {}", windowHandle);
        driver.switchTo().window(windowHandle);
    }

    /**
     * Returns all open window handles.
     */
    public List<String> getAllWindowHandles() {
        return new ArrayList<>(driver.getWindowHandles());
    }

    /**
     * Closes the current window/tab and switches back to the target handle.
     */
    public void closeCurrentTabAndSwitchBack(String targetHandle) {
        driver.close();
        driver.switchTo().window(targetHandle);
    }
}
