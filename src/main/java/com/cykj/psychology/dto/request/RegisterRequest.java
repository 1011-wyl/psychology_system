package com.cykj.psychology.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须为3到20个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度必须为8到72个字符")
    private String password;

    @Min(value = 0, message = "性别参数不正确")
    @Max(value = 2, message = "性别参数不正确")
    private Integer gender;

    @Min(value = 1, message = "年龄参数不正确")
    @Max(value = 120, message = "年龄参数不正确")
    private Integer age;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 200, message = "地址不能超过200个字符")
    private String address;
}
