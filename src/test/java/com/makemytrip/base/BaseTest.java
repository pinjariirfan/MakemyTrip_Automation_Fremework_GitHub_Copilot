package com.makemytrip.base;

import com.makemytrip.config.ConfigReader;
import com.makemytrip.driver.DriverFactory;
import com.makemytrip.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected final Logger logger = LogManager.getLogger(this.getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger.info("================ Starting Test Setup ================");
        WebDriver driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);

        String appUrl = ConfigReader.getUrl();
        logger.info("Navigating to application URL: {}", appUrl);
        driver.get(appUrl);
        logger.info("================ Test Setup Completed ================");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("================ Cleaning up WebDriver ================");
        DriverManager.quitDriver();
        logger.info("================ Teardown Completed ================");
    }

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}
