package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightWaitsTest {
    @Nested
    class WaitingForState {
        // KREN: If you are just checking some property or attribute of the elements in the page,
        // you'll need to add the explicit wait
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");

            // Wait technique #1: Wait for specific element that we know will appear.
            // Either of the following works. You don't need both
            page.waitForSelector("[data-test=product-name]"); // the product name
            page.waitForSelector(".card-img-top"); // images
        }

        @Test
        void shouldShowAllProductNames(Page page) {
            List<String> productNames = page.getByTestId("product-name").allInnerTexts(); // allInnerTexts() trims out the whitespaces surrounding the retrieved texts.
            Assertions.assertThat(productNames).contains("Combination Pliers",
                    "Pliers",
                    "Bolt Cutters",
                    "Long Nose Pliers",
                    "Slip Joint Pliers",
                    "Claw Hammer with Shock Reduction Grip",
                    "Hammer",
                    "Claw Hammer",
                    "Thor Hammer");
        }


        @Test
        void shouldShowAllProductImages(Page page) {
            List<String> productImageTitles = page.locator(".card-img-top").all()
                    .stream()
                    .map(img -> img.getAttribute("alt"))
                    .toList();

            Assertions.assertThat(productImageTitles).contains("Combination Pliers",
                    "Pliers",
                    "Bolt Cutters",
                    "Long Nose Pliers",
                    "Slip Joint Pliers",
                    "Claw Hammer with Shock Reduction Grip",
                    "Hammer",
                    "Claw Hammer",
                    "Thor Hammer");
        }
    }

    @Nested
    class AutomaticWaits {
        // KREN: No need to do any explicit waits if we're actually interacting with the element (ex. click(), fill(), etc.)
        // Playwright will implicitly wait until the element appears.

        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        @DisplayName("Should wait for the filter checkbox options to appear before clicking")
        void shouldWaitForTheFilterCheckboxes(Page page){
            var screwDriverFilter = page.getByLabel("Screwdriver");

            screwDriverFilter.click();

            assertThat(screwDriverFilter).isChecked();
        }

        @Test
        @DisplayName("Should filter products by category")
        void shouldFilterProductsByCategory(Page page) {
            page.getByRole(AriaRole.MENUBAR).getByText("Categories").click();
            page.getByRole(AriaRole.MENUBAR).getByText("Power Tools").click();


            // Need to wait for the elements to appear
            page.waitForSelector(".card"); // <-- simple version and works OK

            /* This helps whe you want to wait for elements to become visible, hidden, or some other state.
            page.waitForSelector(".card",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(2000)
            );
             */

            // Then check the things we need to assert
            var filteredProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(filteredProducts).contains("Sheet Sander", "Belt Sander", "Cordless Drill 18V");

        }
    }

    @Nested
    class WaitingForElementsToAppearAndDisappear {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        @DisplayName("It should display a toaster message when an item is added to the cart")
        void shouldDisplayToasterMessage(Page page) {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            // Wait for the toaster message to appear
            assertThat(page.getByRole(AriaRole.ALERT)).isVisible(); // assumption: Most toaster messages have aria role of alert
            assertThat(page.getByRole(AriaRole.ALERT)).hasText("Product added to shopping cart.");

            // Wait for the toaster message to disappear (don't really need to check this in real life)
            // just for practice/demo
            page.waitForCondition(() -> page.getByRole(AriaRole.ALERT).isHidden());
        }

        @Test
        @DisplayName("Should update the card item count")
        void shouldUpdateCardItemCount(Page page) {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            // Wait for the item count to be updated
            // Technique #1 (Java)
            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("1"));
            // Technique #2 (Playwright selector format)
            //page.waitForSelector("[data-test=cart-quantity]:has-text('1')");
        }
    }

    @Nested
    class WaitingForAPICalls {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        void sortByDescendingPrice(Page page){
            // Sort by descending price and wait for the API Response
            // https://api.practicesoftwaretesting.com/products?page=0&sort=price,desc&between=price,1,100&is_rental=false
            page.waitForResponse("**/products?page=0&sort**",
                    () -> {
                        page.getByTestId("sort").selectOption("Price (High - Low)");
                        //page.getByTestId("product-price").first().waitFor(); // <-- here is a wait. but this is not enough.
                    });

            // Find all the prices on the page
            var productPrices = page.getByTestId("product-price")
                    .allInnerTexts()
                    .stream()
                    .map(WaitingForAPICalls::extractPrice)
                    .toList();

            // Are the prices in the correct order
            System.out.println("ProductPrices: " + productPrices);
            Assertions.assertThat(productPrices)
                    .isNotEmpty()
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        private static double extractPrice(String price) {
            return Double.parseDouble(price.replace("$", ""));
        }
    }
}
