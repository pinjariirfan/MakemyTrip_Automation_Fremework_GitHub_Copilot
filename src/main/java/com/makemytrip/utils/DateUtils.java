package com.makemytrip.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    /**
     * Returns a LocalDate in the next month (same day of month if valid, or adjusted).
     */
    public static LocalDate getTargetDateNextMonth(int dayOfMonth) {
        LocalDate nextMonthDate = LocalDate.now().plusMonths(1);
        int maxDayInMonth = nextMonthDate.lengthOfMonth();
        int safeDay = Math.min(dayOfMonth, maxDayInMonth);
        return nextMonthDate.withDayOfMonth(safeDay);
    }

    /**
     * Returns a LocalDate 30 days from today.
     */
    public static LocalDate getDateNextMonth() {
        return LocalDate.now().plusMonths(1);
    }

    /**
     * Formats date to match MakeMyTrip aria-label format: "Wed Oct 21 2026"
     */
    public static String formatForAriaLabel(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    /**
     * Formats date to "MMMM yyyy" e.g., "October 2026"
     */
    public static String formatMonthYear(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    /**
     * Formats date to "MMM dd, yyyy"
     */
    public static String formatDisplay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }
}
