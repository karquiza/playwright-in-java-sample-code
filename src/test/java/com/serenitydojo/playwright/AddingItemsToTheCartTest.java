package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AddingItemsToTheCartTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

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

        playwright.selectors().setTestIdAttribute("data-test"); // KREN: Not sure where to set this actually.
        // Update: yeah, missed that in the video, but this is the correct location for this.
    }

    @BeforeEach
    public void setup(){
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers() {
        // KREN: The purpose of this exercise is to use all the different kinds of locators in the lesson.
        //       The question of what is the best locator to use and best practices will be taken up in a future lesson.

        page.navigate("https://practicesoftwaretesting.com/");
        page.getByPlaceholder("Search").fill("pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        assertThat(page.locator(".card")).hasCount(4);
        assertThat(page.getByTestId("product-name")).hasCount(4);

        List<String> productNames = page.getByTestId("product-name").allTextContents();
        // KREN: Uses AssertJ assertion instead of Junit assertion
        Assertions.assertThat(productNames).allMatch(s -> s.contains("Pliers"));

        Locator outOfStockItem = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");

        assertThat(outOfStockItem).hasCount(1);
        assertThat(outOfStockItem).hasText("Long Nose Pliers");
    }

    @DisplayName("Search for hammers")
    @Test
    void searchForHammers() {
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByPlaceholder("Search").fill("hammer");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        assertThat(page.locator(".card")).hasCount(7);
        assertThat(page.getByTestId("product-name")).hasCount(7);

        List<String> productNames = page.getByTestId("product-name").allTextContents();
        Assertions.assertThat(productNames).allMatch(s -> s.contains("Hammer") || s.contains("hammer"));
        productNames.forEach(System.out::println);
    }
}
