package com.cykj.psychology.service;

import com.cykj.psychology.entity.*;
import java.util.List;
import java.util.Map;

public interface AdminService {

    // 管理员登录
    SysAdmin login(String username, String password);

    // 获取用户列表
    List<SysUser> getUserList();

    // 获取用户统计
    int getUserCount();

    // 更新用户状态
    void updateUserStatus(Long userId, Integer status);

    // 重置用户密码
    String resetUserPassword(Long userId);

    // 获取咨询师列表
    List<SysCounselor> getCounselorList();

    // 添加咨询师
    String addCounselor(SysCounselor counselor);

    // 更新咨询师
    void updateCounselor(SysCounselor counselor);

    // 删除咨询师
    void deleteCounselor(Long id);

    // 获取预约列表
    List<AppointmentRecord> getAppointmentList();

    // 终止预约
    void terminateAppointment(Long id);

    // 获取统计报表
    Map<String, Object> getStatistics();
}
