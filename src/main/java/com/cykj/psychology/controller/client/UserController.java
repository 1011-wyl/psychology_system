package com.cykj.psychology.controller.client;

import com.cykj.psychology.common.Result;
import com.cykj.psychology.dto.request.RechargeRequest;
import com.cykj.psychology.dto.request.RegisterRequest;
import com.cykj.psychology.entity.AccountTransaction;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.service.SysUserService;
import com.cykj.psychology.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/user")
public class UserController {
    private final SysUserService userService;
    private final AccountTransactionMapper transactionMapper;
    private final JwtUtils jwtUtils;
    private final boolean demoRechargeEnabled;

    public UserController(
            SysUserService userService,
            AccountTransactionMapper transactionMapper,
            JwtUtils jwtUtils,
            @Value("${payment.demo-recharge-enabled:false}") boolean demoRechargeEnabled) {
        this.userService = userService;
        this.transactionMapper = transactionMapper;
        this.jwtUtils = jwtUtils;
        this.demoRechargeEnabled = demoRechargeEnabled;
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest body) {
        SysUser user = new SysUser();
        user.setUsername(body.getUsername().trim());
        user.setPassword(body.getPassword());
        user.setGender(body.getGender());
        user.setAge(body.getAge());
        user.setPhone(body.getPhone());
        user.setAddress(body.getAddress());
        if (!userService.register(user)) {
            throw ApiException.conflict("用户名已存在");
        }
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password) {
        SysUser user = userService.login(username, password);
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("balance", user.getBalance());
        data.put("token", jwtUtils.generateToken(user.getId(), "user"));
        return Result.success("登录成功", data);
    }

    @PostMapping("/recharge")
    public Result<Map<String, Object>> recharge(
            @Valid @RequestBody RechargeRequest body,
            HttpServletRequest request) {
        if (!demoRechargeEnabled) {
            throw ApiException.forbidden("充值功能尚未接入支付平台，当前已安全禁用");
        }
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = userService.recharge(userId, body.getAmount());

        Map<String, Object> data = new HashMap<>();
        data.put("balance", user.getBalance());
        return Result.success("充值成功", data);
    }

    @GetMapping("/transactions")
    public Result<List<AccountTransaction>> getTransactions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(transactionMapper.selectByUserId(userId));
    }
}
