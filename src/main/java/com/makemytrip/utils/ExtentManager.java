package com.makemytrip.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    public synchronized static ExtentReports getInstance() {
        if (extent == null) {
            String reportDir = "test-output";
            File dir = new File(reportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String reportPath = reportDir + File.separator + "ExtentReport.html";
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("MakeMyTrip Automation Test Report");
            sparkReporter.config().setReportName("Flight Search Automation Results");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Application", "MakeMyTrip Flights");
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Environment", "QA Automation");
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    public static void removeTest() {
        testThreadLocal.remove();
    }

    public synchronized static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
