package com.makemytrip.pages;

import com.makemytrip.models.FlightDetails;
import com.makemytrip.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightResultsPage extends BasePage {

    // Locators for Post-Search Popups / Overlays
    private final By popupButtons = By.xpath(
            "//button[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'GOT IT') or " +
            "contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'OKAY') or " +
            "contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'LOCK')] | " +
            "//span[contains(@class,'overlayClose') or @data-cy='overlayClose']"
    );

    // Results container and flight cards
    private final By flightCards = By.xpath(
            "//div[contains(@class, 'listingCard') or contains(@class, 'clusterViewPrice') or " +
            "contains(@class, 'fli-list') or contains(@id, 'flight_list_item') or " +
            "contains(@class, 'flightItem') or contains(@class, 'flightCard') or " +
            "contains(@class, 'listing_card') or contains(@class, 'clusterView')]"
    );

    // Flight Card Child Elements Locators (relative)
    private final By airlineNameLocator = By.xpath(
            ".//p[contains(@class,'boldFont') and contains(@class,'blackText')] | " +
            ".//div[contains(@class,'airlineInfo')]//span[contains(@class,'boldFont')] | " +
            ".//p[contains(@class,'airlineName')] | .//span[contains(@class,'airways-name')]"
    );

    private final By flightCodeLocator = By.xpath(
            ".//p[contains(@class,'flightCode')] | .//span[contains(@class,'flightNumber')] | " +
            ".//span[contains(@class,'font11')]"
    );

    private final By depTimeLocator = By.xpath(
            ".//div[contains(@class,'timeInfoLeft')]//span | " +
            ".//div[contains(@class,'timeInfoLeft')]//p | .//div[contains(@class,'flexOne')][1]//font"
    );

    private final By arrTimeLocator = By.xpath(
            ".//div[contains(@class,'timeInfoRight')]//span | " +
            ".//div[contains(@class,'timeInfoRight')]//p | .//div[contains(@class,'flexOne')][2]//font"
    );

    private final By durationLocator = By.xpath(
            ".//div[contains(@class,'stop-info')]//p | .//div[contains(@class,'stop-info')] | " +
            ".//div[contains(@class,'flight-time-info')]//p"
    );

    private final By stopsLocator = By.xpath(
            ".//p[contains(@class,'flights-layover')] | .//div[contains(@class,'stop-info')]//span | " +
            ".//p[contains(@class,'non-stop')] | .//p[contains(text(),'Stop') or contains(text(),'stop')]"
    );

    private final By priceLocator = By.xpath(
            ".//div[contains(@class,'priceSection')]//div[contains(@class,'blackText')] | " +
            ".//div[contains(@class,'clusterViewPrice')] | " +
            ".//p[contains(@class,'actual-price')] | " +
            ".//div[contains(@class,'textRight')]//span[contains(text(),'₹')] | " +
            ".//div[contains(@class,'textRight')]//p[contains(text(),'₹') or contains(@class,'blackText')] | " +
            ".//span[contains(text(),'₹')]"
    );

    // Non-stop Filter locator
    private final By nonStopFilter = By.xpath(
            "//p[contains(text(),'Non Stop') or contains(text(),'0 Stop')]/ancestor::label | " +
            "//span[contains(text(),'Non Stop') or contains(text(),'0 Stop')]/ancestor::label | " +
            "//input[@id='filter_stop_0']/following-sibling::label | " +
            "//div[contains(@id,'stops')]//label[contains(.,'Non Stop') or contains(.,'0 Stop')]"
    );

    public FlightResultsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Waits for flight search results to load and dismisses any overlays.
     */
    public void waitForResultsToLoad() {
        logger.info("Waiting for flight search results page navigation...");

        // Wait up to 15 seconds for URL to transition to search page
        long urlWaitStart = System.currentTimeMillis();
        while (System.currentTimeMillis() - urlWaitStart < 15000) {
            if (driver.getCurrentUrl().contains("flight/search") || driver.getCurrentUrl().contains("search")) {
                logger.info("Successfully navigated to flight search URL: {}", driver.getCurrentUrl());
                break;
            }
            WaitUtils.sleep(500);
        }

        // If URL stayed on /flights/, try clicking search again once
        if (!driver.getCurrentUrl().contains("search")) {
            logger.warn("URL did not change to search results, re-attempting submit click...");
            try {
                WebElement btn = driver.findElement(By.xpath("//p[@data-cy='submit']//a | //p[@data-cy='submit']"));
                clickUsingJS(btn);
                WaitUtils.sleep(2000);
            } catch (Exception ignored) {
            }
        }

        // Check if page rendered as raw 200-OK text from Akamai and refresh if so
        try {
            String pageSource = driver.getPageSource();
            if (pageSource != null && (pageSource.contains("200-OK") || pageSource.contains("Pretty-print"))) {
                logger.warn("Detected raw '200-OK' response from server. Refreshing page with established session cookies...");
                WaitUtils.sleep(2000);
                driver.navigate().refresh();
                WaitUtils.sleep(3000);
            }
        } catch (Exception ignored) {
        }

        dismissPopups();

        long startTime = System.currentTimeMillis();
        long timeoutMs = 35000;
        boolean found = false;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            dismissPopups();
            List<WebElement> cards = driver.findElements(flightCards);
            if (!cards.isEmpty()) {
                logger.info("Found {} flight card elements on the page.", cards.size());
                found = true;
                break;
            }
            WaitUtils.sleep(1000);
        }

        if (!found) {
            logger.warn("Polling for flight cards reached timeout, current URL: {}", driver.getCurrentUrl());
        }
        dismissPopups();
    }

    /**
     * Dismisses any dynamic overlays or 'Lock Price' popups.
     */
    public void dismissPopups() {
        // Only dismiss popups if we are on the results page
        if (!driver.getCurrentUrl().contains("search")) {
            return;
        }
        try {
            List<WebElement> buttons = driver.findElements(popupButtons);
            for (WebElement btn : buttons) {
                if (btn.isDisplayed()) {
                    logger.info("Dismissing overlay button with text: '{}'", btn.getText());
                    clickUsingJS(btn);
                    WaitUtils.sleep(500);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Parses all flight cards currently displayed and extracts FlightDetails.
     */
    public List<FlightDetails> getAllFlights() {
        dismissPopups();
        List<WebElement> cards = driver.findElements(flightCards);
        logger.info("Found {} flight card elements on the results page.", cards.size());

        List<FlightDetails> flights = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            WebElement card = cards.get(i);
            try {
                String cardText = card.getText();
                if (cardText == null || cardText.trim().isEmpty()) {
                    continue;
                }

                // Extract Airline
                String airline = extractChildTextOrDefault(card, airlineNameLocator, "Unknown Airline");
                if (airline.contains("\n")) {
                    airline = airline.split("\n")[0].trim();
                }

                // Extract Flight Code
                String flightCode = extractChildTextOrDefault(card, flightCodeLocator, "");

                // Extract Departure Time
                String depTime = extractChildTextOrDefault(card, depTimeLocator, "");

                // Extract Arrival Time
                String arrTime = extractChildTextOrDefault(card, arrTimeLocator, "");

                // Extract Duration
                String duration = extractChildTextOrDefault(card, durationLocator, "");

                // Extract Stops
                String stops = extractChildTextOrDefault(card, stopsLocator, "Non-Stop");

                // Extract Price
                int price = extractPrice(card);

                if (price > 0) {
                    FlightDetails flight = new FlightDetails(airline, flightCode, depTime, arrTime, duration, stops, price);
                    flights.add(flight);
                }
            } catch (Exception e) {
                logger.debug("Skipping flight card #{} due to parsing error: {}", i, e.getMessage());
            }
        }

        logger.info("Successfully extracted {} parsed flight details.", flights.size());
        Collections.sort(flights);
        return flights;
    }

    /**
     * Returns the cheapest flight from the parsed list.
     */
    public FlightDetails getCheapestFlight() {
        List<FlightDetails> flights = getAllFlights();
        if (flights.isEmpty()) {
            throw new RuntimeException("No flights were found on the results page!");
        }
        return flights.get(0);
    }

    /**
     * Returns the second cheapest flight from the parsed list.
     */
    public FlightDetails getSecondCheapestFlight() {
        List<FlightDetails> flights = getAllFlights();
        if (flights.size() < 2) {
            throw new RuntimeException("Less than two flights were found on the results page!");
        }
        return flights.get(1);
    }

    /**
     * Applies the 'Non Stop' filter, waits for results to refresh, and returns filtered flights.
     */
    public List<FlightDetails> applyNonStopFilter() {
        logger.info("Applying 'Non Stop' flight filter...");
        dismissPopups();

        try {
            WebElement filterElement = wait.until(ExpectedConditions.presenceOfElementLocated(nonStopFilter));
            scrollToElement(filterElement);
            clickUsingJS(filterElement);
            logger.info("Successfully clicked 'Non Stop' filter.");
        } catch (Exception e) {
            logger.error("Failed to click 'Non Stop' filter: {}", e.getMessage(), e);
            throw new RuntimeException("Could not apply Non Stop filter", e);
        }

        WaitUtils.sleep(2500);
        dismissPopups();
        return getAllFlights();
    }

    private String extractChildTextOrDefault(WebElement parent, By locator, String defaultValue) {
        try {
            List<WebElement> elements = parent.findElements(locator);
            if (!elements.isEmpty()) {
                String text = elements.get(0).getText().trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    private int extractPrice(WebElement parent) {
        try {
            List<WebElement> priceElements = parent.findElements(priceLocator);
            for (WebElement el : priceElements) {
                String text = el.getText().trim();
                int price = parsePriceString(text);
                if (price > 0) {
                    return price;
                }
            }
            return parsePriceString(parent.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    private int parsePriceString(String text) {
        if (text == null || text.isEmpty()) return 0;
        Pattern pattern = Pattern.compile("[₹Rs.]*\\s*([0-9]{1,2}(?:,[0-9]{2,3})+|[0-9]{4,6})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String digitsOnly = matcher.group(1).replace(",", "").trim();
            try {
                return Integer.parseInt(digitsOnly);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }
}
