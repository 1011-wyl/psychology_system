package com.cykj.psychology.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SysUser {

  private Long id;

  private String username;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private Integer gender;  // long改为Integer，数据库用TINYINT
  private Integer age;     // long改为Integer

  private String phone;
  private String address;

  private BigDecimal balance;  // double改为BigDecimal，金额用这个更精确

  private Integer status;  // long改为Integer

  private Date createTime;      // java.sql.Timestamp改为java.util.Date
  private Date updateTime;      // java.sql.Timestamp改为java.util.Date

}
