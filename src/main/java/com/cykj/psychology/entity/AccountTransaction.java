package com.cykj.psychology.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountTransaction {
    private Long id;
    private Long userId;
    private Long counselorId;
    private Long relatedId;
    private String relatedType;
    private Integer transactionType; // 0-收入 1-支出
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private Date createTime;
}