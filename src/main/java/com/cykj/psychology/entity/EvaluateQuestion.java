package com.cykj.psychology.entity;

import lombok.Data;

@Data
public class EvaluateQuestion {
    private Long id;
    private Long fieldId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer scoreA;
    private Integer scoreB;
    private Integer scoreC;
    private Integer scoreD;
    private Integer sortOrder;
    private Integer status;
}