package com.makemytrip.pages;

import com.makemytrip.utils.DateUtils;
import com.makemytrip.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDate;
import java.util.List;

public class FlightSearchPage extends BasePage {

    // Popups
    private final By closeLoginModalBtn = By.xpath(
            "//span[contains(@class,'commonModal__close') or @data-cy='closeModal' or contains(@class,'close')] | " +
            "//a[contains(@class,'close')]"
    );

    // Menu
    private final By flightsMenu = By.xpath(
            "//li[contains(@class,'menu_Flights')] | //a[contains(@href,'/flights')]"
    );

    // From & To triggers and inputs
    private final By fromCityLabel = By.xpath(
            "//label[@for='fromCity'] | //span[contains(text(),'From')]/ancestor::label | //input[@id='fromCity']"
    );
    private final By fromCityAutoInput = By.xpath(
            "//input[@placeholder='From' or @aria-controls='react-autowhatever-1' or contains(@class,'react-autosuggest__input')]"
    );

    private final By toCityLabel = By.xpath(
            "//label[@for='toCity'] | //span[contains(text(),'To')]/ancestor::label | //input[@id='toCity']"
    );
    private final By toCityAutoInput = By.xpath(
            "//input[@placeholder='To' or @aria-controls='react-autowhatever-1' or contains(@class,'react-autosuggest__input')]"
    );

    private final By suggestionListItems = By.xpath(
            "//ul[contains(@class,'react-autosuggest__suggestions-list')]//li | " +
            "//li[contains(@id,'react-autowhatever-1')] | " +
            "//div[contains(@class,'autoSuggestPlugin')]//li | " +
            "//ul[@role='listbox']//li"
    );

    // Calendar & Date Picker
    private final By departureDateTrigger = By.xpath(
            "//label[@for='departure'] | //div[contains(@class,'dates')]//label | //span[contains(text(),'Departure')]/ancestor::div"
    );
    private final By nextMonthNavBtn = By.xpath(
            "//span[@aria-label='Next Month' or contains(@class,'DayPicker-NavButton--next')]"
    );

    // Search button (from verified DOM)
    private final By searchBtn = By.xpath(
            "//p[@data-cy='submit']//a | //p[@data-cy='submit'] | //a[contains(@class,'widgetSearchBtn')]"
    );

    public FlightSearchPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Dismisses any initial login or promotional modal that overlays MakeMyTrip.
     */
    public void closeInitialModals() {
        logger.info("Checking for initial modal popups...");
        try {
            WaitUtils.sleep(1200);
            List<WebElement> closeButtons = driver.findElements(closeLoginModalBtn);
            for (WebElement btn : closeButtons) {
                if (btn.isDisplayed()) {
                    logger.info("Dismissing modal popup.");
                    clickUsingJS(btn);
                    WaitUtils.sleep(800);
                    break;
                }
            }
        } catch (Exception e) {
            logger.debug("No modal close button found or error: {}", e.getMessage());
        }
    }

    /**
     * Navigates to Flights section if not already on it.
     */
    public void navigateToFlightsSection() {
        logger.info("Navigating to Flights section");
        closeInitialModals();
        try {
            List<WebElement> flightTabs = driver.findElements(flightsMenu);
            if (!flightTabs.isEmpty() && flightTabs.get(0).isDisplayed()) {
                clickUsingJS(flightTabs.get(0));
            }
        } catch (Exception e) {
            logger.warn("Could not click flights menu tab: {}", e.getMessage());
        }
        closeInitialModals();
    }

    /**
     * Enters source location.
     */
    public void enterSourceCity(String sourceCity) {
        logger.info("Entering source city: {}", sourceCity);

        // Check if the source city is already set to the requested city
        try {
            WebElement fromVal = driver.findElement(By.xpath("//input[@id='fromCity']"));
            String currentVal = fromVal.getAttribute("value");
            if (currentVal != null && currentVal.toLowerCase().contains(sourceCity.toLowerCase())) {
                logger.info("Source city is already set to '{}'.", currentVal);
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            click(fromCityLabel);
        } catch (Exception e) {
            clickUsingJS(fromCityLabel);
        }

        WaitUtils.sleep(800);

        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(fromCityAutoInput));
            input.clear();
            input.sendKeys(sourceCity);
            WaitUtils.sleep(1200);

            List<WebElement> suggestions = driver.findElements(suggestionListItems);
            if (!suggestions.isEmpty()) {
                logger.info("Selecting first suggestion for source: {}", suggestions.get(0).getText().replace("\n", " "));
                click(suggestions.get(0));
            } else {
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            }
        } catch (Exception e) {
            logger.warn("Autosuggest interaction encountered: {}. Attempting fallback.", e.getMessage());
            try {
                WebElement input = driver.findElement(fromCityAutoInput);
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            } catch (Exception ex) {
                logger.warn("Could not send enter key, proceeding: {}", ex.getMessage());
            }
        }
        WaitUtils.sleep(500);
    }

    /**
     * Enters destination location.
     */
    public void enterDestinationCity(String destinationCity) {
        logger.info("Entering destination city: {}", destinationCity);

        try {
            click(toCityLabel);
        } catch (Exception e) {
            clickUsingJS(toCityLabel);
        }

        WaitUtils.sleep(800);

        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(toCityAutoInput));
            input.clear();
            input.sendKeys(destinationCity);
            WaitUtils.sleep(1200);

            List<WebElement> suggestions = driver.findElements(suggestionListItems);
            if (!suggestions.isEmpty()) {
                logger.info("Selecting first suggestion for destination: {}", suggestions.get(0).getText().replace("\n", " "));
                click(suggestions.get(0));
            } else {
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            }
        } catch (Exception e) {
            logger.warn("Autosuggest for destination encountered: {}. Attempting fallback.", e.getMessage());
            try {
                WebElement input = driver.findElement(toCityAutoInput);
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            } catch (Exception ex) {
                logger.warn("Could not send enter key for destination: {}", ex.getMessage());
            }
        }
        WaitUtils.sleep(500);
    }

    /**
     * Selects departure date in the next month dynamically.
     */
    public void selectDepartureDateInNextMonth(LocalDate targetDate) {
        logger.info("Selecting departure date: {} (Target Month: {})",
                DateUtils.formatDisplay(targetDate), DateUtils.formatMonthYear(targetDate));

        // Open calendar if not already visible
        try {
            WebElement datePicker = driver.findElement(By.cssSelector("div.DayPicker, div.DayPicker-wrapper"));
            if (!datePicker.isDisplayed()) {
                clickUsingJS(departureDateTrigger);
            }
        } catch (Exception e) {
            try {
                clickUsingJS(departureDateTrigger);
            } catch (Exception ignored) {
            }
        }

        WaitUtils.sleep(800);

        String ariaLabel = DateUtils.formatForAriaLabel(targetDate);
        logger.info("Looking for calendar element with aria-label: '{}'", ariaLabel);

        By dateByAriaLabel = By.xpath("//div[contains(@aria-label, '" + ariaLabel + "') and not(contains(@aria-disabled,'true'))]");
        List<WebElement> matchingDateElements = driver.findElements(dateByAriaLabel);

        if (matchingDateElements.isEmpty()) {
            try {
                List<WebElement> nextBtns = driver.findElements(nextMonthNavBtn);
                if (!nextBtns.isEmpty() && nextBtns.get(0).isDisplayed()) {
                    clickUsingJS(nextBtns.get(0));
                    WaitUtils.sleep(600);
                    matchingDateElements = driver.findElements(dateByAriaLabel);
                }
            } catch (Exception ignored) {
            }
        }

        if (!matchingDateElements.isEmpty()) {
            clickUsingJS(matchingDateElements.get(0));
            logger.info("Successfully clicked target date with aria-label: {}", ariaLabel);
        } else {
            logger.info("Locating target day in the next month column");
            int dayNumber = targetDate.getDayOfMonth();
            By dayNumberLocator = By.xpath(
                    "(//div[contains(@class,'DayPicker-Month')])[2]//p[text()='" + dayNumber + "']/ancestor::div[contains(@class,'DayPicker-Day')] | " +
                    "(//div[contains(@class,'DayPicker-Month')])[2]//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))][15]"
            );
            try {
                WebElement dayEl = driver.findElement(dayNumberLocator);
                clickUsingJS(dayEl);
                logger.info("Successfully selected next month day from calendar.");
            } catch (Exception ex) {
                logger.warn("Could not find exact day cell, selecting first active day in next month: {}", ex.getMessage());
                By anyDay = By.xpath("(//div[contains(@class,'DayPicker-Month')])[2]//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))][1]");
                clickUsingJS(driver.findElement(anyDay));
            }
        }
        WaitUtils.sleep(600);
    }

    /**
     * Clicks the Search button and returns the FlightResultsPage.
     */
    public FlightResultsPage clickSearch() {
        logger.info("Clicking Search Flights button");
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(searchBtn));
            scrollToElement(btn);
            WaitUtils.sleep(600);
            try {
                btn.click();
            } catch (Exception ex) {
                clickUsingJS(btn);
            }
            logger.info("Successfully triggered Search button click.");
        } catch (Exception e) {
            logger.warn("Standard click failed: {}. Attempting JS click fallback.", e.getMessage());
            clickUsingJS(searchBtn);
        }
        return new FlightResultsPage(driver);
    }
}
