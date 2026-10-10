package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserMapper {

    // 查询所有用户
    List<SysUser> selectAll();

    // 根据ID查询
    SysUser selectById(@Param("id") Long id);

    // 根据用户名查询
    SysUser selectByUsername(@Param("username") String username);

    // 插入用户
    int insert(SysUser user);

    // 更新用户
    int update(SysUser user);

    // 删除用户
    int deleteById(@Param("id") Long id);

    int updateBalance(@Param("id") Long id, @Param("balance") java.math.BigDecimal balance);

    @Update("UPDATE sys_user SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE sys_user SET balance = COALESCE(balance, 0) + #{amount}, update_time = NOW() WHERE id = #{id}")
    int increaseBalance(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);

    @Update("UPDATE sys_user SET balance = balance - #{amount}, update_time = NOW() " +
            "WHERE id = #{id} AND COALESCE(balance, 0) >= #{amount}")
    int deductBalanceIfSufficient(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);
}
