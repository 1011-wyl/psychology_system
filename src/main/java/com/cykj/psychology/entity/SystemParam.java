package com.cykj.psychology.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SystemParam {
    private Long id;
    private String paramName;
    private String paramType;
    private String paramValue;
    private Integer status;
    private Date createTime;
}
