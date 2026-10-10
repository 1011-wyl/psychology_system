package com.cykj.psychology.entity;

import lombok.Data;
import java.util.Date;

@Data
public class EvaluateField {
    private Long id;
    private String fieldName;
    private String description;
    private Integer status;
}