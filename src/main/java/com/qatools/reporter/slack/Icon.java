package com.qatools.reporter.slack;

public class Icon {
    final public static String passed = ":white_check_mark:";
    final public static String failed = ":x:";
    final public static String skipped = "🔵";

    public static String of(String value) {
        switch (value.toLowerCase()) {
            case "failed":
                return failed;
            case "skipped":
                return skipped;
            default:
                return passed;
        }
    }
}
