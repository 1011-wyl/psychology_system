package com.cykj.psychology.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class EvaluationSubmitRequest {
    @NotNull(message = "请选择评估领域")
    private Long fieldId;

    @NotEmpty(message = "评估答案不能为空")
    private Map<String, String> answers;
}
