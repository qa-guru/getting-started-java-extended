import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.options.WaitForSelectorState.HIDDEN;
import static com.microsoft.playwright.options.WaitForSelectorState.VISIBLE;
import static org.junit.jupiter.api.Assertions.*;

public class PlaywrightLoginTests {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private final String url = Paths.get("src/test/resources/pages/login.html").toUri().toString();

    @BeforeEach
    void setupTest() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
        page.setDefaultTimeout(5000);
        page.navigate(url);
    }

    @AfterEach
    void teardown() {
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void successfulLoginTest() {
        page.fill("#username", "admin");
        page.fill("#password", "admin");
        page.click("#loginButton");

        assertTrue(page.isVisible("#message"));
        assertTrue(page.innerText("#message").contains("Login successful!"));
        assertTrue(page.getAttribute("#message", "class").contains("success"));

        page.waitForSelector(".login-container", new Page.WaitForSelectorOptions().setState(HIDDEN));
        assertFalse(page.isVisible(".login-container"));
        assertEquals("Welcome, admin!", page.innerText("#greeting"));
        assertTrue(page.isVisible("#logoutButton"));
    }

    @Test
    void successfulLoginWithEnterTest() {
        page.fill("#username", "admin");
        page.fill("#password", "admin");
        page.press("#password", "Enter");

        assertTrue(page.isVisible("#message"));
        assertTrue(page.innerText("#message").contains("Login successful!"));
        assertTrue(page.getAttribute("#message", "class").contains("success"));

        page.waitForSelector(".login-container", new Page.WaitForSelectorOptions().setState(HIDDEN));
        assertFalse(page.isVisible(".login-container"));
        assertEquals("Welcome, admin!", page.innerText("#greeting"));
        assertTrue(page.isVisible("#logoutButton"));
    }

    @Test
    void emptyUsernameTest() {
        page.fill("#password", "admin");
        page.click("#loginButton");

        assertTrue(page.isVisible("#message"));
        assertTrue(page.innerText("#message").contains("Username is required."));
        assertTrue(page.getAttribute("#message", "class").contains("error"));
    }

    @Test
    void emptyPasswordTest() {
        page.fill("#username", "admin");
        page.click("#loginButton");

        assertTrue(page.isVisible("#message"));
        assertTrue(page.innerText("#message").contains("Password is required."));
        assertTrue(page.getAttribute("#message", "class").contains("error"));
    }

    @Test
    void emptyUsernameAndPasswordTest() {
        page.click("#loginButton");

        assertTrue(page.isVisible("#message"));
        assertTrue(page.innerText("#message").contains("Username and Password are required."));
        assertTrue(page.getAttribute("#message", "class").contains("error"));
    }

    @Test
    void invalidCredentialsTest() {
        page.fill("#username", "admin");
        page.fill("#password", "wrongpassword");
        page.click("#loginButton");

        assertTrue(page.isVisible("#message"));
        assertTrue(page.innerText("#message").contains("Invalid username or password"));
        assertTrue(page.getAttribute("#message", "class").contains("error"));
    }

    @Test
    void logoutTest() {
        page.fill("#username", "admin");
        page.fill("#password", "admin");
        page.click("#loginButton");

        page.waitForSelector("#logoutButton");
        assertTrue(page.isVisible("#logoutButton"));

        page.click("#logoutButton");

        page.waitForSelector(".login-container", new Page.WaitForSelectorOptions().setState(VISIBLE));
        assertTrue(page.isVisible(".login-container"));
        assertEquals("", page.innerText("#greeting"));
        assertFalse(page.isVisible("#logoutButton"));
    }
}
