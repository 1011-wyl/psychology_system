package com.cykj.psychology.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SysCounselor {
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String realName;
    private String phone;
    private String title;
    private String avatar;
    private BigDecimal fee;
    private String description;
    private BigDecimal balance;
    private BigDecimal rating;
    private Long roleId;
    private Integer status;
    private Date createTime;
}
