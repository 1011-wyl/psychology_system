package com.cykj.psychology.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AppointmentRecord {
    private Long id;
    private Long userId;
    private Long counselorId;
    private Long fieldId;
    private Date appointmentDate;
    private String appointmentTime;
    private String problemDesc;
    private String problemImages;
    private Integer status;
    private BigDecimal fee;
    private Date confirmTime;
    private String diagnosisReply;
    private Date replyTime;
    private String evaluation;
    private Date evaluationTime;
    private Date createTime;
}