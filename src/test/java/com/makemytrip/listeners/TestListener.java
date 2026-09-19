package com.makemytrip.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.makemytrip.driver.DriverManager;
import com.makemytrip.utils.ExtentManager;
import com.makemytrip.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite '{}' started.", context.getName());
        ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        logger.info(">>> Starting Test: {}", testName);

        ExtentTest extentTest = ExtentManager.getInstance().createTest(testName, description);
        ExtentManager.setTest(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("<<< Test PASSED: {}", result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.PASS, "Test executed successfully: " + result.getMethod().getMethodName());
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.error("<<< Test FAILED: {} | Reason: {}", testName, result.getThrowable().getMessage());

        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());

            WebDriver driver = DriverManager.getDriver();
            if (driver != null) {
                String base64Screenshot = ScreenshotUtils.captureScreenshotAsBase64(driver);
                if (base64Screenshot != null) {
                    test.fail("Failure Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                }
                ScreenshotUtils.captureScreenshotAsFile(driver, testName);
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("<<< Test SKIPPED: {}", result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "Test Skipped: " + result.getThrowable().getMessage());
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite '{}' finished.", context.getName());
        ExtentManager.flush();
    }
}
