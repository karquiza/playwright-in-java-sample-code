package com.serenitydojo.playwright;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@UsePlaywright(AnAnnotatedPlaywrightTest.CustomOptions.class)
public class AnAnnotatedPlaywrightTest {

    public static class CustomOptions implements OptionsFactory {
        @Override
        public Options getOptions(){
            return new Options()
                    .setHeadless(false)
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox",
                                            "--disable-gpu",
                                            "--disable-extensions"))
                    );
        }
    }

    @Test
    void shouldShowThePageTitle(Page page) {
        page.navigate("https://practicesoftwaretesting.com");
        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void shouldSearchByKeyword(Page page) {
        page.navigate("https://practicesoftwaretesting.com");

        page.locator("[placeholder=Search]").fill("pliers");
        page.locator("button:has-text('Search')").click();

        int matchingSearchResults = page.locator(".card").count();
        Assertions.assertTrue(matchingSearchResults > 0);
    }
}
