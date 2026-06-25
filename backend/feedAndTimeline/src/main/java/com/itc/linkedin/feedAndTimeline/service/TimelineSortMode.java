package com.itc.linkedin.feedAndTimeline.service;

public enum TimelineSortMode {
    TOP,
    RECENT;

    public static TimelineSortMode from(String value) {
        if (value == null || value.isBlank()) {
            return TOP;
        }

        return "recent".equalsIgnoreCase(value) ? RECENT : TOP;
    }
}
