package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.LoadState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightAssertionsTest {
    // KREN: This is about using different kinds of assertions (ex. PlaywrightAssertions vs AssertJ assertions)

    @DisplayName("Making assertions about the contents of a field")
    @Nested
    class LocatingElementsUsingCSS {

        @BeforeEach
        void openContactPage(Page page){
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Checking the value of a field")
        @Test
        void fieldValues(Page page) {
            var firstNameField = page.getByLabel("First name");

            firstNameField.fill("Sarah-Jane");

            assertThat(firstNameField).hasValue("Sarah-Jane"); // This uses PlaywrightAssertions.assertThat because the parameter is a page locator
            // assertThat(firstNameField).isDisabled(); // This will fail and it will take longer to run
            assertThat(firstNameField).not().isDisabled(); // notice the "not()" part, which is cool
            assertThat(firstNameField).isVisible();
            assertThat(firstNameField).isEditable();
        }
    }

    @DisplayName("Making assertions about data values")
    @Nested
    class MakingAssertionsAboutDataValues {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
        }

        @Test
        void allProductPricesShouldBeCorrectValues(Page page) {
            List<Double> productPrices = page.getByTestId("product-price").allInnerTexts()
                    .stream().map(price -> Double.parseDouble(price.replace("$", "")))
                    .toList();

            Assertions.assertThat(productPrices) // This one uses AssertJ assertion for all other general assertions
                    .isNotEmpty()
                    .allMatch(price -> price > 0)
                    .doesNotContain(0.0) // same as the previous one, but just another way of checking it
                    .allMatch(price -> price < 1000)
                    .allSatisfy(price -> // same as the previous checks, but another way of checking
                            Assertions.assertThat(price)
                                    .isGreaterThan(0.0)
                                    .isLessThan(1000.0)
                    );
        }

        @Test
        void shouldSortInAlphabeticalOrder(Page page) {
            // Get the locator for the Sort select field
            var sortOrderField = page.getByTestId("sort");

            // Select an option and check that the correct sort order was actually selected
            sortOrderField.selectOption("Name (A - Z)");
            assertThat(sortOrderField).hasValue("name,asc");

            // KREN: Pretty sure that some form of waiting is needed here...
            page.waitForLoadState(LoadState.NETWORKIDLE); // OK, so the tutorial added the wait with the caveat that
                                                        // waiting for network to become idle isn't the ideal way of waiting.
                                                        // But we can.

            // Get the text of all the cards (names of the products)
            // and use AssertJ to assert that they are in alphabetical order
            List<String> itemNames = page.getByTestId("product-name").allTextContents();

            // 2 ways to check this
            Assertions.assertThat(itemNames).isSortedAccordingTo(Comparator.naturalOrder());
            Assertions.assertThat(itemNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
        }

        @Test
        void shouldSortInReverseAlphabeticalOrder(Page page) {
            // Get the locator for the Sort select field
            var sortOrderField = page.getByTestId("sort");

            // Select an option and check that the correct sort order was actually selected
            sortOrderField.selectOption("Name (Z - A)");
            assertThat(sortOrderField).hasValue("name,desc");

            // KREN: Pretty sure that some form of waiting is needed here...
            page.waitForLoadState(LoadState.NETWORKIDLE); // OK, so the tutorial added the wait with the caveat that
            // waiting for network to become idle isn't the ideal way of waiting.
            // But we can.

            // Get the text of all the cards (names of the products)
            // and use AssertJ to assert that they are in alphabetical order
            List<String> itemNames = page.getByTestId("product-name").allTextContents();

            Assertions.assertThat(itemNames).isSortedAccordingTo(Comparator.reverseOrder());
        }
    }
}
