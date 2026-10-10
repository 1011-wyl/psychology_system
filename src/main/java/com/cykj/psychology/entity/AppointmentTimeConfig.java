package com.cykj.psychology.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AppointmentTimeConfig {
    private Long id;
    private Long counselorId;
    private Date configDate;
    private String timeSlot;
    private Integer isBooked;
    private Long userId;
    private Date createTime;
}