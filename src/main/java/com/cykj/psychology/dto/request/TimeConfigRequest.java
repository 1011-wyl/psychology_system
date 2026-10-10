package com.cykj.psychology.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class TimeConfigRequest {
    @NotBlank(message = "日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式不正确")
    private String date;

    @NotEmpty(message = "时间段不能为空")
    @Size(max = 24, message = "单日时间段不能超过24个")
    private List<@Pattern(
            regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d-(?:[01]\\d|2[0-3]):[0-5]\\d$",
            message = "时间段格式不正确") String> timeSlots;
}
