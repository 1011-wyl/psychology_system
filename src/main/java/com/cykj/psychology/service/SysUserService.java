package com.cykj.psychology.service;

import com.cykj.psychology.entity.SysUser;

import java.math.BigDecimal;
import java.util.List;



public interface SysUserService {

    // 查询所有用户
    List<SysUser> findAll();

    // 根据ID查询
    SysUser findById(Long id);

    // 根据用户名查询
    SysUser findByUsername(String username);

    // 用户注册
    boolean register(SysUser user);

    // 用户登录
    SysUser login(String username, String password);

    // 更新用户信息
    boolean update(SysUser user);

    SysUser recharge(Long userId, BigDecimal amount);
}
