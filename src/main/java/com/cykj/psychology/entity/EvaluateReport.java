package com.cykj.psychology.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class EvaluateReport {
    private Long id;
    private Long userId;
    private Long fieldId;
    private Integer totalScore;
    private String conclusion;
    private String reportContent;
    private BigDecimal fee;
    private Date createTime;
}