package com.cykj.psychology.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AppointmentBookRequest {
    @NotNull(message = "请选择咨询师")
    private Long counselorId;

    @NotNull(message = "请选择咨询领域")
    private Long fieldId;

    @NotBlank(message = "请选择预约日期")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "预约日期格式不正确")
    private String date;

    @NotBlank(message = "请选择预约时间")
    @Pattern(
            regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d-(?:[01]\\d|2[0-3]):[0-5]\\d$",
            message = "预约时间格式不正确")
    private String timeSlot;

    @NotBlank(message = "请填写问题描述")
    @Size(max = 1000, message = "问题描述不能超过1000个字符")
    private String problemDesc;
}
