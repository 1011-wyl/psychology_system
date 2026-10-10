package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.SysAdmin;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.entity.SysUser;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysAdminMapper {

    // 管理员登录
    @Select("SELECT * FROM sys_admin WHERE username = #{username}")
    SysAdmin selectByUsername(String username);

    @Select("SELECT * FROM sys_admin WHERE id = #{id}")
    SysAdmin selectById(Long id);

    @Update("UPDATE sys_admin SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    // 查询所有用户
    @Select("SELECT * FROM sys_user WHERE status != 2 ORDER BY id")
    List<SysUser> selectAllUsers();

    // 查询用户总数
    @Select("SELECT COUNT(*) FROM sys_user WHERE status != 2")
    int countUsers();

    // 更新用户状态（启用/禁用）
    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);

    // 重置用户密码
    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{userId}")
    int resetUserPassword(@Param("userId") Long userId, @Param("password") String password);

    // 查询所有咨询师
    @Select("SELECT * FROM sys_counselor WHERE status != 2")
    List<SysCounselor> selectAllCounselors();

    // 添加咨询师
    @Insert("INSERT INTO sys_counselor(username, password, real_name, phone, title, fee, description, status, role_id, create_time) " +
            "VALUES(#{username}, #{password}, #{realName}, #{phone}, #{title}, #{fee}, #{description}, 1, 2, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCounselor(SysCounselor counselor);

    // 更新咨询师
    @Update("UPDATE sys_counselor SET real_name=#{realName}, phone=#{phone}, title=#{title}, fee=#{fee}, description=#{description} WHERE id=#{id}")
    int updateCounselor(SysCounselor counselor);

    // 删除咨询师（逻辑删除）
    @Update("UPDATE sys_counselor SET status = 2 WHERE id = #{id}")
    int deleteCounselor(Long id);

    // 查询所有预约
    @Select("SELECT * FROM appointment_record ORDER BY create_time DESC")
    List<AppointmentRecord> selectAllAppointments();

    // 终止预约
    @Update("UPDATE appointment_record SET status = 5 WHERE id = #{id} AND status IN (0, 2)")
    int terminateAppointment(Long id);
}
