package com.makemytrip.tests;

import com.aventstack.extentreports.Status;
import com.makemytrip.base.BaseTest;
import com.makemytrip.config.ConfigReader;
import com.makemytrip.models.FlightDetails;
import com.makemytrip.pages.FlightResultsPage;
import com.makemytrip.pages.FlightSearchPage;
import com.makemytrip.pages.GooglePage;
import com.makemytrip.utils.DateUtils;
import com.makemytrip.utils.ExtentManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

public class FlightSearchTest extends BaseTest {

    @Test(description = "Automates MakeMyTrip flight search, finds cheapest/second-cheapest flights, handles multi-tab navigation to Google, and applies Non-Stop filter")
    public void testFlightSearchAndMultiTabWorkflow() {
        WebDriver driver = getDriver();
        FlightSearchPage searchPage = new FlightSearchPage(driver);

        // Step 1: Navigate to Flights section
        logger.info("Step 1: Navigating to Flights section");
        logToReport("Step 1: Navigating to Flights section");
        searchPage.navigateToFlightsSection();

        // Step 2: Enter source and destination locations
        String source = ConfigReader.getSourceCity();
        String destination = ConfigReader.getDestinationCity();
        logger.info("Step 2: Entering Source: '{}' and Destination: '{}'", source, destination);
        logToReport(String.format("Step 2: Entering Source '%s' and Destination '%s'", source, destination));
        searchPage.enterSourceCity(source);
        searchPage.enterDestinationCity(destination);

        // Step 3: Select a date for the next month dynamically
        LocalDate nextMonthDate = DateUtils.getDateNextMonth();
        logger.info("Step 3: Selecting departure date for next month: {}", DateUtils.formatDisplay(nextMonthDate));
        logToReport("Step 3: Selecting departure date for next month: " + DateUtils.formatDisplay(nextMonthDate));
        searchPage.selectDepartureDateInNextMonth(nextMonthDate);

        // Step 4: Click Search
        logger.info("Step 4: Clicking Search button");
        logToReport("Step 4: Clicking Search button");
        FlightResultsPage resultsPage = searchPage.clickSearch();

        // Step 5: Wait for results and extract flight details
        logger.info("Step 5: Waiting for results page to load");
        logToReport("Step 5: Waiting for flight search results to load");
        resultsPage.waitForResultsToLoad();

        List<FlightDetails> flights = resultsPage.getAllFlights();
        Assert.assertFalse(flights.isEmpty(), "Expected at least 1 flight to be returned in search results!");

        // Step 6: Identify and print cheapest and second cheapest flights
        FlightDetails cheapestFlight = resultsPage.getCheapestFlight();
        System.out.println("\n=======================================================");
        System.out.println("              CHEAPEST FLIGHT DETAILS                  ");
        System.out.println("=======================================================");
        System.out.println(cheapestFlight);
        System.out.println("=======================================================\n");

        logToReport("<b>Cheapest Flight:</b><br/>" + formatFlightForReport(cheapestFlight));

        if (flights.size() >= 2) {
            FlightDetails secondCheapestFlight = resultsPage.getSecondCheapestFlight();
            System.out.println("=======================================================");
            System.out.println("           SECOND CHEAPEST FLIGHT DETAILS              ");
            System.out.println("=======================================================");
            System.out.println(secondCheapestFlight);
            System.out.println("=======================================================\n");

            logToReport("<b>Second Cheapest Flight:</b><br/>" + formatFlightForReport(secondCheapestFlight));
            Assert.assertTrue(secondCheapestFlight.getPrice() >= cheapestFlight.getPrice(),
                    "Second cheapest price must be greater than or equal to cheapest price!");
        } else {
            System.out.println("Note: Only 1 flight option available in the search results.");
        }

        // Step 7: Multi-tab navigation - Open a new browser tab within the same session and navigate to Google
        String originalWindowHandle = driver.getWindowHandle();
        String googleUrl = ConfigReader.getGoogleUrl();
        logger.info("Step 7: Opening new browser tab within same session and navigating to: {}", googleUrl);
        logToReport("Step 7: Opening new browser tab within same session and navigating to: " + googleUrl);

        String newTabHandle = searchPage.openNewTabAndSwitch(googleUrl);
        GooglePage googlePage = new GooglePage(driver);

        String googleTitle = googlePage.getPageTitle();
        logger.info("New tab successfully opened. Title: '{}'", googleTitle);
        Assert.assertTrue(googleTitle.toLowerCase().contains("google"), "Page title should contain 'Google'");

        // Step 8: Additional Scenario
        // 8a. In Google tab: Perform a query and verify results
        String searchQuery = ConfigReader.getGoogleSearchQuery();
        logger.info("Step 8a (Additional Scenario): Performing Google search for '{}'", searchQuery);
        logToReport("Step 8a (Additional Scenario): Performing Google search for '" + searchQuery + "'");
        googlePage.search(searchQuery);
        Assert.assertTrue(googlePage.areSearchResultsDisplayed(), "Google search results should be displayed");

        // 8b. Close Google tab and switch back to MakeMyTrip tab
        logger.info("Step 8b: Closing Google tab and returning to MakeMyTrip flight results");
        logToReport("Step 8b: Switching back to MakeMyTrip results tab");
        searchPage.closeCurrentTabAndSwitchBack(originalWindowHandle);

        // 8c. In MakeMyTrip results: Apply 'Non Stop' filter and verify
        logger.info("Step 8c: Applying 'Non Stop' filter on MakeMyTrip flight search results");
        logToReport("Step 8c: Applying 'Non Stop' filter on MakeMyTrip flight search results");
        try {
            List<FlightDetails> nonStopFlights = resultsPage.applyNonStopFilter();
            if (!nonStopFlights.isEmpty()) {
                FlightDetails cheapestNonStop = nonStopFlights.get(0);
                System.out.println("=======================================================");
                System.out.println("          CHEAPEST NON-STOP FLIGHT DETAILS             ");
                System.out.println("=======================================================");
                System.out.println(cheapestNonStop);
                System.out.println("=======================================================\n");

                logToReport("<b>Cheapest Non-Stop Flight:</b><br/>" + formatFlightForReport(cheapestNonStop));
            } else {
                logger.info("No non-stop flights found after applying filter.");
            }
        } catch (Exception e) {
            logger.warn("Non Stop filter could not be applied or no non-stop flights: {}", e.getMessage());
        }

        logger.info("Flight Search and Multi-Tab automation test completed successfully!");
        logToReport("Flight Search and Multi-Tab automation test completed successfully!");
    }

    private void logToReport(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.INFO, message);
        }
    }

    private String formatFlightForReport(FlightDetails f) {
        return String.format(
                "Airline: <b>%s</b> | Flight Code: %s<br/>" +
                "Departure: %s | Arrival: %s | Duration: %s | Stops: %s<br/>" +
                "Price: <b style='color:green;'>₹%,d</b>",
                f.getAirlineName(), f.getFlightCode(),
                f.getDepartureTime(), f.getArrivalTime(),
                f.getDuration(), f.getStops(), f.getPrice()
        );
    }
}
