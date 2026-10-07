package com.makemytrip.driver;

import com.makemytrip.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverFactory {
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase().trim();
        boolean headless = ConfigReader.isHeadless();
        logger.info("Initializing WebDriver for browser: {} | Headless: {}", browser, headless);

        WebDriver driver;
        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                edgeOptions.addArguments("--disable-notifications");
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/154.0.0.0 Safari/537.36");

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.default_content_setting_values.notifications", 2);
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                chromeOptions.setExperimentalOption("prefs", prefs);

                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }

                ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);

                // Mask navigator.webdriver via CDP script evaluation on new documents
                try {
                    Map<String, Object> cdpParams = new HashMap<>();
                    cdpParams.put("source",
                            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                            "window.chrome = { runtime: {} };"
                    );
                    chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", cdpParams);
                } catch (Exception e) {
                    logger.warn("Could not execute CDP command for stealth: {}", e.getMessage());
                }

                driver = chromeDriver;
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            logger.warn("Could not maximize window: {}", e.getMessage());
        }

        return driver;
    }
}
