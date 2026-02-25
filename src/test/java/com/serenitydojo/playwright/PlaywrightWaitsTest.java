package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightWaitsTest {

    @BeforeEach
    void openHomePage(Page page) { page.navigate("https://practicesoftwaretesting.com"); }

    @Test
    void shouldShowAllProductNames(Page page) {

    }

    @Test
    void shouldShowAllProductImages(Page page) {
        
    }
}
