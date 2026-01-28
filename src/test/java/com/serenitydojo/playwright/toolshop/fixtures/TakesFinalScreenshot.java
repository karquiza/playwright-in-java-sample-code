package com.serenitydojo.playwright.toolshop.fixtures;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

// The UsePlaywright annotation isn't strictly necessary here,
// but it can help with some IDE errors
@UsePlaywright(ChromeHeadlessOptions.class)
@ExtendWith(FailureRecorder.class)
public interface TakesFinalScreenshot {

    @AfterEach
    default void takeScreenshot(Page page, TestInfo testInfo)  {
        if (FailureRecorder.hasFailed(testInfo)) {
            ScreenshotManager.takeScreenshot(page, "Final screenshot");
        }
    }
}
