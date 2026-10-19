package com.cykj.psychology.utils;

import com.cykj.psychology.exception.ApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validation shared by appointment booking and counselor schedule management.
 */
public final class AppointmentTimeUtils {
    private static final String TIME_SLOT_PATTERN =
            "^(?:[01]\\d|2[0-3]):[0-5]\\d-(?:[01]\\d|2[0-3]):[0-5]\\d$";

    private AppointmentTimeUtils() {
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw ApiException.badRequest("日期格式不正确");
        }
    }

    public static TimeRange parseTimeSlot(String timeSlot) {
        if (timeSlot == null || !timeSlot.matches(TIME_SLOT_PATTERN)) {
            throw ApiException.badRequest("时间段格式不正确");
        }
        String[] parts = timeSlot.split("-", -1);
        if (parts.length != 2) {
            throw ApiException.badRequest("时间段格式不正确");
        }
        try {
            LocalTime start = LocalTime.parse(parts[0]);
            LocalTime end = LocalTime.parse(parts[1]);
            if (!start.isBefore(end)) {
                throw ApiException.badRequest("时间段结束时间必须晚于开始时间");
            }
            return new TimeRange(start, end);
        } catch (DateTimeParseException exception) {
            throw ApiException.badRequest("时间段格式不正确");
        }
    }

    public static void validateBookable(LocalDate date, String timeSlot, LocalDateTime now) {
        TimeRange range = parseTimeSlot(timeSlot);
        if (date.isBefore(now.toLocalDate())
                || (date.isEqual(now.toLocalDate()) && !range.getStart().isAfter(now.toLocalTime()))) {
            throw ApiException.badRequest("不能预约已经开始的时间段");
        }
    }

    public static boolean hasStarted(LocalDate date, String timeSlot, LocalDateTime now) {
        TimeRange range = parseTimeSlot(timeSlot);
        return date.isBefore(now.toLocalDate())
                || (date.isEqual(now.toLocalDate()) && !range.getStart().isAfter(now.toLocalTime()));
    }

    public static void validateTimeSlots(List<String> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty()) {
            throw ApiException.badRequest("时间段不能为空");
        }

        Set<String> uniqueSlots = new HashSet<>(timeSlots);
        if (uniqueSlots.size() != timeSlots.size()) {
            throw ApiException.badRequest("时间段不能重复");
        }

        List<TimeRange> ranges = new ArrayList<>();
        for (String timeSlot : timeSlots) {
            ranges.add(parseTimeSlot(timeSlot));
        }
        ranges.sort(Comparator.comparing(TimeRange::getStart));
        for (int index = 1; index < ranges.size(); index++) {
            if (ranges.get(index).getStart().isBefore(ranges.get(index - 1).getEnd())) {
                throw ApiException.badRequest("时间段不能重叠");
            }
        }
    }

    public static final class TimeRange {
        private final LocalTime start;
        private final LocalTime end;

        private TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalTime getStart() {
            return start;
        }

        public LocalTime getEnd() {
            return end;
        }
    }
}
