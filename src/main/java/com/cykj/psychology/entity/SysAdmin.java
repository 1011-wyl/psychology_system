package com.cykj.psychology.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Date;

@Data
public class SysAdmin {
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String realName;
    private String phone;
    private Long roleId;
    private Integer status;
    private Date createTime;
}
