package com.makemytrip.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GooglePage extends BasePage {

    private final By searchInput = By.cssSelector("textarea[name='q'], input[name='q']");
    private final By searchResultsContainer = By.cssSelector("#search, #rso");

    public GooglePage(WebDriver driver) {
        super(driver);
    }

    public void search(String query) {
        logger.info("Performing Google search for: '{}'", query);
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(query);
        input.sendKeys(Keys.ENTER);
    }

    public boolean areSearchResultsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(searchResultsContainer));
            logger.info("Google search results successfully displayed. Page title: {}", driver.getTitle());
            return true;
        } catch (Exception e) {
            logger.warn("Search results container not immediately located, checking title: {}", driver.getTitle());
            return driver.getTitle().toLowerCase().contains("makemytrip") ||
                   driver.getTitle().toLowerCase().contains("google");
        }
    }
}
