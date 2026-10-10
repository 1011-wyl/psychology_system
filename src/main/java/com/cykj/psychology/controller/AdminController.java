package com.cykj.psychology.controller;

import com.cykj.psychology.common.Result;
import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.SysAdmin;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.service.AdminService;
import com.cykj.psychology.utils.JwtUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final JwtUtils jwtUtils;

    public AdminController(AdminService adminService, JwtUtils jwtUtils) {
        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password) {
        SysAdmin admin = adminService.login(username, password);
        if (admin == null) {
            return Result.error(401, "用户名或密码错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("realName", admin.getRealName());
        data.put("token", jwtUtils.generateToken(admin.getId(), "admin"));
        return Result.success("登录成功", data);
    }

    @GetMapping("/users")
    public Result<List<SysUser>> getUsers() {
        return Result.success(adminService.getUserList());
    }

    @GetMapping("/users/count")
    public Result<Integer> getUserCount() {
        return Result.success(adminService.getUserCount());
    }

    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(
            @PathVariable Long id,
            @RequestParam @Min(0) @Max(2) Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success("更新成功");
    }

    @PostMapping("/users/{id}/reset-password")
    public Result<Map<String, String>> resetUserPassword(@PathVariable Long id) {
        String temporaryPassword = adminService.resetUserPassword(id);
        return Result.success(
                "密码已重置，请安全地交给用户",
                Collections.singletonMap("temporaryPassword", temporaryPassword)
        );
    }

    @GetMapping("/counselors")
    public Result<List<SysCounselor>> getCounselors() {
        return Result.success(adminService.getCounselorList());
    }

    @PostMapping("/counselors")
    public Result<Map<String, String>> addCounselor(@RequestBody SysCounselor counselor) {
        validateCounselor(counselor, true);
        String temporaryPassword = adminService.addCounselor(counselor);
        return Result.success(
                "添加成功，请安全地交付临时密码",
                Collections.singletonMap("temporaryPassword", temporaryPassword)
        );
    }

    @PutMapping("/counselors/{id}")
    public Result<String> updateCounselor(
            @PathVariable Long id,
            @RequestBody SysCounselor counselor) {
        validateCounselor(counselor, false);
        counselor.setId(id);
        adminService.updateCounselor(counselor);
        return Result.success("更新成功");
    }

    @DeleteMapping("/counselors/{id}")
    public Result<String> deleteCounselor(@PathVariable Long id) {
        adminService.deleteCounselor(id);
        return Result.success("删除成功");
    }

    @GetMapping("/appointments")
    public Result<List<AppointmentRecord>> getAppointments() {
        return Result.success(adminService.getAppointmentList());
    }

    @PostMapping("/appointments/{id}/terminate")
    public Result<String> terminateAppointment(@PathVariable Long id) {
        adminService.terminateAppointment(id);
        return Result.success("已终止预约");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminService.getStatistics());
    }

    private void validateCounselor(SysCounselor counselor, boolean creating) {
        if (creating && (counselor.getUsername() == null || counselor.getUsername().trim().isEmpty())) {
            throw ApiException.badRequest("用户名不能为空");
        }
        if (counselor.getRealName() == null || counselor.getRealName().trim().isEmpty()) {
            throw ApiException.badRequest("姓名不能为空");
        }
        BigDecimal fee = counselor.getFee();
        if (fee == null || fee.signum() < 0 || fee.compareTo(new BigDecimal("100000")) > 0) {
            throw ApiException.badRequest("咨询费用不正确");
        }
    }
}
