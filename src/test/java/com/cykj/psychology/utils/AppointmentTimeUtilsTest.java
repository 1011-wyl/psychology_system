package com.cykj.psychology.utils;

import com.cykj.psychology.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentTimeUtilsTest {
    @Test
    void rejectsImpossibleCalendarDate() {
        ApiException exception = assertThrows(
                ApiException.class,
                () -> AppointmentTimeUtils.parseDate("2026-02-30")
        );
        assertEquals(400, exception.getStatus());
    }

    @Test
    void rejectsSlotWhoseEndIsNotAfterItsStart() {
        assertThrows(ApiException.class, () -> AppointmentTimeUtils.parseTimeSlot("10:00-09:00"));
        assertThrows(ApiException.class, () -> AppointmentTimeUtils.parseTimeSlot("10:00-10:00"));
    }

    @Test
    void rejectsDuplicateAndOverlappingScheduleSlots() {
        assertThrows(
                ApiException.class,
                () -> AppointmentTimeUtils.validateTimeSlots(Arrays.asList("09:00-10:00", "09:00-10:00"))
        );
        assertThrows(
                ApiException.class,
                () -> AppointmentTimeUtils.validateTimeSlots(Arrays.asList("09:00-10:30", "10:00-11:00"))
        );
        assertDoesNotThrow(
                () -> AppointmentTimeUtils.validateTimeSlots(Arrays.asList("09:00-10:00", "10:00-11:00"))
        );
    }

    @Test
    void rejectsPastOrAlreadyStartedBooking() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 19, 10, 30);
        assertThrows(
                ApiException.class,
                () -> AppointmentTimeUtils.validateBookable(LocalDate.of(2026, 10, 19), "10:00-11:00", now)
        );
        assertDoesNotThrow(
                () -> AppointmentTimeUtils.validateBookable(LocalDate.of(2026, 10, 19), "11:00-12:00", now)
        );
    }
}
