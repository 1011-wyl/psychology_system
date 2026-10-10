package com.cykj.psychology.controller;

import com.cykj.psychology.common.Result;
import com.cykj.psychology.dto.request.DiagnosisRequest;
import com.cykj.psychology.dto.request.TimeConfigRequest;
import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.AppointmentTimeConfig;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.service.CounselorService;
import com.cykj.psychology.utils.JwtUtils;
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
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/counselor")
public class CounselorController {
    private final CounselorService counselorService;
    private final JwtUtils jwtUtils;

    public CounselorController(CounselorService counselorService, JwtUtils jwtUtils) {
        this.counselorService = counselorService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password) {
        SysCounselor counselor = counselorService.login(username, password);
        if (counselor == null) {
            return Result.error(401, "用户名或密码错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", counselor.getId());
        data.put("realName", counselor.getRealName());
        data.put("token", jwtUtils.generateToken(counselor.getId(), "counselor"));
        return Result.success("登录成功", data);
    }

    @GetMapping("/appointments")
    public Result<List<AppointmentRecord>> getAppointments(HttpServletRequest request) {
        return Result.success(counselorService.getAppointments(currentCounselorId(request)));
    }

    @GetMapping("/appointment/{id}")
    public Result<AppointmentRecord> getAppointmentDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        return Result.success(counselorService.getAppointment(currentCounselorId(request), id));
    }

    @PostMapping("/confirm")
    public Result<String> confirmAppointment(
            @RequestParam Long appointmentId,
            HttpServletRequest request) {
        counselorService.confirmAppointment(currentCounselorId(request), appointmentId);
        return Result.success("确认成功");
    }

    @PostMapping("/diagnose")
    public Result<String> diagnoseAppointment(
            @Valid @RequestBody DiagnosisRequest body,
            HttpServletRequest request) {
        counselorService.diagnoseAppointment(
                currentCounselorId(request),
                body.getAppointmentId(),
                body.getReply()
        );
        return Result.success("回复成功");
    }

    @GetMapping("/time-config")
    public Result<List<AppointmentTimeConfig>> getTimeConfig(
            @RequestParam String date,
            HttpServletRequest request) {
        return Result.success(counselorService.getTimeConfig(currentCounselorId(request), date));
    }

    @PostMapping("/time-config")
    public Result<String> saveTimeConfig(
            @Valid @RequestBody TimeConfigRequest body,
            HttpServletRequest request) {
        counselorService.saveTimeConfig(
                currentCounselorId(request),
                body.getDate(),
                body.getTimeSlots()
        );
        return Result.success("设置成功");
    }

    @GetMapping("/info")
    public Result<SysCounselor> getInfo(HttpServletRequest request) {
        return Result.success(counselorService.getCounselorById(currentCounselorId(request)));
    }

    private Long currentCounselorId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
}
