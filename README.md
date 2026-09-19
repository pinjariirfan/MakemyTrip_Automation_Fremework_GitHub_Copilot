# MakeMyTrip Automation Framework (Page Object Model)

A robust, enterprise-grade test automation framework built using **Java 21**, **Selenium WebDriver 4**, **TestNG**, **Maven**, and **ExtentReports 5** following the **Page Object Model (POM)** design pattern.

---

## 🚀 Key Features

- **Page Object Model (POM)**: Clean architectural separation between UI locators/actions and test logic.
- **Dynamic Date Handling**: Calculates and selects departure dates for the upcoming month dynamically using `java.time.LocalDate`.
- **Intelligent Overlay Handling**: Resiliently dismisses initial login prompts, discount modals, and post-search promotional popups.
- **Data Model & Sorting**: Encapsulates flight listings in `FlightDetails` model objects and sorts by numerical price to accurately find the cheapest and second-cheapest flights.
- **Multi-Tab Orchestration**: Uses Selenium 4's native `WindowType.TAB` to open and switch between tabs within the same browser session.
- **Additional Verification Scenario**: Validates Google search in a secondary tab, switches back to the travel portal, applies the **Non-Stop** flight filter, and prints the cheapest non-stop flight.
- **Thread-Safe Driver Management**: Implements `ThreadLocal<WebDriver>` for test isolation and parallel execution readiness.
- **Anti-Bot Stealth**: Configures Chrome with CDP-based `navigator.webdriver` masking and automation flag bypass.
- **Rich HTML Reporting**: Automatically generates detailed HTML execution reports and captures screenshots on test failure using **ExtentReports 5**.

---

## 📁 Project Structure

```
MakemyTrip_Automation_Fremework_GitHub_Copilot/
├── pom.xml                               # Maven project dependencies & build configuration
├── testng.xml                            # TestNG suite runner
├── src/
│   ├── main/
│   │   ├── java/com/makemytrip/
│   │   │   ├── config/
│   │   │   │   └── ConfigReader.java     # Centralized configuration properties loader
│   │   │   ├── driver/
│   │   │   │   ├── DriverFactory.java    # Browser instantiation with stealth options
│   │   │   │   └── DriverManager.java    # ThreadLocal WebDriver storage
│   │   │   ├── models/
│   │   │   │   └── FlightDetails.java    # Flight POJO with Comparable price sorting
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java         # Common actions, explicit waits, tab switching
│   │   │   │   ├── FlightSearchPage.java # Locators & actions for flight search & date selection
│   │   │   │   ├── FlightResultsPage.java# Parsing cards, extracting prices, applying filters
│   │   │   │   └── GooglePage.java       # Google search tab automation
│   │   │   └── utils/
│   │   │       ├── DateUtils.java        # Dynamic calendar date calculations
│   │   │       ├── ExtentManager.java    # ExtentReports instance management
│   │   │       ├── ScreenshotUtils.java  # Screenshot capture on test failure
│   │   │       └── WaitUtils.java        # Explicit & Fluent wait utilities
│   │   └── resources/
│   │       ├── config.properties         # Browser, URL, timeouts, and search parameters
│   │       └── log4j2.xml                # Logging configuration
│   └── test/
│       ├── java/com/makemytrip/
│       │   ├── base/
│       │   │   └── BaseTest.java         # TestNG @BeforeMethod & @AfterMethod lifecycle
│       │   ├── listeners/
│       │   │   └── TestListener.java     # Test status reporting & failure screenshot hook
│       │   └── tests/
│       │       └── FlightSearchTest.java # End-to-end automated test flow
│       └── resources/
│           └── testng.xml
└── README.md
```

---

## 💻 How to Run (Visual Execution in Terminal)

Open a **PowerShell** terminal in the project directory and run:

```powershell
mvn clean test
```

Or to run specifically using the TestNG suite file:

```powershell
mvn test -DsuiteXmlFile=testng.xml
```

### Headless vs Visual Mode:
In `src/main/resources/config.properties`:
- `headless=false` (default): Launches a real, maximized Chrome browser window so you can watch every action live on screen.
- `headless=true`: Runs silently in background for CI/CD environments.

---

## 📊 Viewing Test Reports
After execution, open the generated HTML report:
- **Extent Report**: `test-output/ExtentReport.html`
- **Surefire Reports**: `target/surefire-reports/index.html`
