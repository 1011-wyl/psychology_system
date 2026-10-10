package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.SysCounselor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysCounselorMapper {

    // 查询所有咨询师（启用状态）
    @Select("SELECT * FROM sys_counselor WHERE status = 1")
    List<SysCounselor> selectAll();

    // 根据ID查询咨询师
    @Select("SELECT * FROM sys_counselor WHERE id = #{id}")
    SysCounselor selectById(Long id);

    //根据用户名查询咨询师信息
    @Select("SELECT * FROM sys_counselor WHERE username = #{username}")
    SysCounselor selectByUsername(String username);

    // 更新咨询师信息
    @Update("UPDATE sys_counselor SET real_name=#{realName}, phone=#{phone}, title=#{title}, " +
            "fee=#{fee}, description=#{description}, balance=#{balance}, rating=#{rating}, " +
            "status=#{status}, update_time=NOW() WHERE id=#{id}")
    int update(SysCounselor counselor);

    @Update("UPDATE sys_counselor SET password = #{password}, update_time = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE sys_counselor SET balance = COALESCE(balance, 0) + #{amount}, update_time = NOW() WHERE id = #{id}")
    int increaseBalance(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);

    @Update("UPDATE sys_counselor SET balance = balance - #{amount}, update_time = NOW() " +
            "WHERE id = #{id} AND COALESCE(balance, 0) >= #{amount}")
    int deductBalanceIfSufficient(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);
}
