package com.cykj.psychology.controller;

import com.cykj.psychology.common.Result;
import com.cykj.psychology.dto.CounselorPublicDto;
import com.cykj.psychology.dto.request.AppointmentBookRequest;
import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.service.AppointmentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/counselors")
    public Result<List<CounselorPublicDto>> getCounselors() {
        return Result.success(appointmentService.getCounselors().stream()
                .map(CounselorPublicDto::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/counselor/{id}")
    public Result<CounselorPublicDto> getCounselor(@PathVariable Long id) {
        return Result.success(CounselorPublicDto.from(appointmentService.getCounselorById(id)));
    }

    @GetMapping("/time-slots")
    public Result<List<String>> getTimeSlots(
            @RequestParam Long counselorId,
            @RequestParam
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式不正确")
            String date) {
        return Result.success(appointmentService.getAvailableTimeSlots(counselorId, date));
    }

    @PostMapping("/book")
    public Result<AppointmentRecord> book(
            @Valid @RequestBody AppointmentBookRequest body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        AppointmentRecord record = appointmentService.createAppointment(
                userId,
                body.getCounselorId(),
                body.getFieldId(),
                body.getDate(),
                body.getTimeSlot(),
                body.getProblemDesc()
        );
        return Result.success(record);
    }

    @GetMapping("/list")
    public Result<List<AppointmentRecord>> getMyAppointments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(appointmentService.getUserAppointments(userId));
    }
}
