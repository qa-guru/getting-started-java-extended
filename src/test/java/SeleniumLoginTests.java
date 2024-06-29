import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumLoginTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String url = Paths.get("src/test/resources/pages/login.html").toUri().toString();

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofMillis(5000));
        driver.get(url);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void successfulLoginTest() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("loginButton")).click();

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertTrue(message.getText().contains("Login successful!"));
        assertTrue(message.getAttribute("class").contains("success"));

        WebElement loginContainer = driver.findElement(By.className("login-container"));
        wait.until(ExpectedConditions.invisibilityOf(loginContainer));
        assertFalse(loginContainer.isDisplayed());

        WebElement greeting = driver.findElement(By.id("greeting"));
        assertEquals("Welcome, admin!", greeting.getText());

        WebElement logoutButton = driver.findElement(By.id("logoutButton"));
        assertTrue(logoutButton.isDisplayed());
    }

    @Test
    void successfulLoginWithEnterTest() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys(Keys.RETURN);

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertTrue(message.getText().contains("Login successful!"));
        assertTrue(message.getAttribute("class").contains("success"));

        WebElement loginContainer = driver.findElement(By.className("login-container"));
        wait.until(ExpectedConditions.invisibilityOf(loginContainer));
        assertFalse(loginContainer.isDisplayed());

        WebElement greeting = driver.findElement(By.id("greeting"));
        assertEquals("Welcome, admin!", greeting.getText());

        WebElement logoutButton = driver.findElement(By.id("logoutButton"));
        assertTrue(logoutButton.isDisplayed());
    }

    @Test
    void emptyUsernameTest() {
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("loginButton")).click();

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertTrue(message.getText().contains("Username is required."));
        assertTrue(message.getAttribute("class").contains("error"));
    }

    @Test
    void emptyPasswordTest() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("loginButton")).click();

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertTrue(message.getText().contains("Password is required."));
        assertTrue(message.getAttribute("class").contains("error"));
    }

    @Test
    void emptyUsernameAndPasswordTest() {
        driver.findElement(By.id("loginButton")).click();

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertTrue(message.getText().contains("Username and Password are required."));
        assertTrue(message.getAttribute("class").contains("error"));
    }

    @Test
    void invalidCredentialsTest() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.id("loginButton")).click();

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertTrue(message.getText().contains("Invalid username or password."));
        assertTrue(message.getAttribute("class").contains("error"));
    }

    @Test
    void logoutTest() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("loginButton")).click();

        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("logoutButton")));
        logoutButton.click();

        WebElement loginContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-container")));
        assertTrue(loginContainer.isDisplayed());

        WebElement greeting = driver.findElement(By.id("greeting"));
        assertEquals("", greeting.getText());

        assertFalse(logoutButton.isDisplayed());
    }
}
