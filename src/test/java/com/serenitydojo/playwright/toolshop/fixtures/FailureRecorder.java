package com.serenitydojo.playwright.toolshop.fixtures;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FailureRecorder implements TestWatcher {
    private static final ConcurrentMap<String, Boolean> failures = new ConcurrentHashMap<>();

    private static String keyFor(ExtensionContext ctx) {
        return ctx.getRequiredTestClass().getName() + "#" + ctx.getRequiredTestMethod().getName();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        failures.put(keyFor(context), true);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        failures.remove(keyFor(context));
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        failures.remove(keyFor(context));
    }

    public static boolean hasFailed(TestInfo info) {
        if (info.getTestClass().isPresent() && info.getTestMethod().isPresent()) {
            String key = info.getTestClass().get().getName() + "#" + info.getTestMethod().get().getName();
            return failures.getOrDefault(key, false);
        }
        return false;
    }
}