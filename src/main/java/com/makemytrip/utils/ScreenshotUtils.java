package com.makemytrip.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);

    public static String captureScreenshotAsBase64(WebDriver driver) {
        if (driver == null) {
            return null;
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture base64 screenshot: {}", e.getMessage());
            return null;
        }
    }

    public static String captureScreenshotAsFile(WebDriver driver, String screenshotName) {
        if (driver == null) {
            return null;
        }
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = screenshotName + "_" + timestamp + ".png";
            Path dirPath = Paths.get("test-output", "screenshots");
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path destination = dirPath.resolve(fileName);
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), destination);
            logger.info("Screenshot saved successfully to {}", destination.toAbsolutePath());
            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            logger.error("Failed to save screenshot file: {}", e.getMessage());
            return null;
        }
    }
}
