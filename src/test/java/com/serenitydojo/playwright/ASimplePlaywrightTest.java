package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;

public class ASimplePlaywrightTest {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;

    Page page;

    @BeforeAll
    public static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox",
                                "--disable-gpu",
                                "--disable-extensions"))
        );
        browserContext = browser.newContext();
    }

    @BeforeEach
    public void setup(){
        page = browserContext.newPage();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void shouldShowThePageTitle() {
        page.navigate("https://practicesoftwaretesting.com");
        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void shouldSearchByKeyword() {
        page.navigate("https://practicesoftwaretesting.com");

        page.locator("[placeholder=Search]").fill("pliers");
        page.locator("button:has-text('Search')").click();

        int matchingSearchResults = page.locator(".card").count();
        Assertions.assertTrue(matchingSearchResults > 0);
    }
}
