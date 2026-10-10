package com.cykj.psychology.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class DiagnosisRequest {
    @NotNull(message = "预约ID不能为空")
    private Long appointmentId;

    @NotBlank(message = "诊断回复不能为空")
    @Size(max = 5000, message = "诊断回复不能超过5000个字符")
    private String reply;
}
