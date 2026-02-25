package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightFormsTest {
    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {

        @BeforeEach
        void openContactPage(Page page){
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Input fields")
        @Test
        void fieldValues(Page page){
            // KREN: Useful to use the labels that the users can actually see to identify fields.
            var firstNameField = page.getByLabel("First name"); // Get the input field that has the visible label text "First name"
            var lastNameField = page.getByLabel("Last name"); // Same for "Last name"

            firstNameField.fill("Sarah-Jane");
            lastNameField.fill("Smith");

            assertThat(firstNameField).hasValue("Sarah-Jane"); // "hasValue()" checks the actual value when you inspect the page source
            assertThat(lastNameField).hasValue("Smith");
        }

        @DisplayName("Complete the form")
        @Test
        void completeForm(Page page){
            // KREN: This kind of does similar to the fieldValues test case,
            // but the tutorial also shows how to use drop-downs ('select' elements)
            // as well as the `textarea` elements

            // KREN: The nice thing is that you don't have to declare what kind of UI element this is at the start;
            // however, you do need to use the correct methods to set and get the values.
            var firstNameField = page.getByLabel("First name");
            var lastNameField = page.getByLabel("Last name");
            var emailField = page.getByLabel("Email address");
            var subjectField = page.getByLabel("Subject");
            var messageField = page.getByLabel("Message");
            var uploadField = page.getByLabel("Attachment");

            firstNameField.fill("Sarah-Jane");
            lastNameField.fill("Smith");
            emailField.fill("Sarah-Jane@gmail.com");
            subjectField.selectOption("Warranty");  // This is a drop-down list ('select').
                                                            // You can select the option using what the user sees,
                                                            // but checking the value will need you to check the page source <option value="option"> tag
            messageField.fill("This is a textarea element, but the fill() method works just as well.");

            // Uploading a file - get the "Path from Source/Root"
            Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").getPath());
            page.setInputFiles("#attachment", fileToUpload);

            assertThat(firstNameField).hasValue("Sarah-Jane");
            assertThat(lastNameField).hasValue("Smith");
            assertThat(emailField).hasValue("Sarah-Jane@gmail.com");
            assertThat(subjectField).hasValue("warranty"); // Case in point: The value of this option is 'warranty' but the text seen by the user is 'Warranty'
            assertThat(messageField).hasValue("This is a textarea element, but the fill() method works just as well.");

            String uploadedFile = uploadField.inputValue();
            org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");
        }

        @DisplayName("Mandatory Fields")
        @Test
        void mandatoryFields(Page page){
            var firstNameField = page.getByLabel("First name");
            var lastNameField = page.getByLabel("Last name");
            var emailField = page.getByLabel("Email address");
            var subjectField = page.getByLabel("Subject");
            var messageField = page.getByLabel("Message");

            var sendButton = page.getByText("Send");

            sendButton.click();

            // Check that error messages are displayed for required/mandatory fields
            var errorMessageFirstName = page.getByRole(AriaRole.ALERT).getByText("First name is required");
            var errorMessageLastName = page.getByRole(AriaRole.ALERT).getByText("Last name is required");
            var errorMessageEmail = page.getByRole(AriaRole.ALERT).getByText("Email is required");
            var errorMessageMessage = page.getByRole(AriaRole.ALERT).getByText("Message is required");

            assertThat(errorMessageFirstName).isVisible();
            assertThat(errorMessageLastName).isVisible();
            assertThat(errorMessageEmail).isVisible();
            assertThat(errorMessageMessage).isVisible();
        }

        @DisplayName("Mandatory Fields - Parameterized Test")
        @ParameterizedTest
        @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
        void mandatoryFieldsParameterizedTest(String fieldName, Page page) {
            var firstNameField = page.getByLabel("First name");
            var lastNameField = page.getByLabel("Last name");
            var emailField = page.getByLabel("Email address"); // This field can also be found by just using "Email"
                                                                    // as the label since there's no conflict within the page
            var subjectField = page.getByLabel("Subject");
            var messageField = page.getByLabel("Message");

            var sendButton = page.getByText("Send");

            // Fill in the field values
            firstNameField.fill("Sarah-Jane");
            lastNameField.fill("Smith");
            emailField.fill("Sarah-Jane@gmail.com");
            subjectField.selectOption("Warranty"); // This is a drop-down, so we need to handle this separately. OOS for now.
            messageField.fill("This is a textarea element, but the fill() method works just as well.");

            // Clear one of the fields
            page.getByLabel(fieldName).fill("");

            // Submit form
            sendButton.click();

            // Check error message
            var errorMessage = page.getByRole(AriaRole.ALERT).getByText(String.format("%s is required", fieldName));

            assertThat(errorMessage).isVisible();
        }

    }
}
