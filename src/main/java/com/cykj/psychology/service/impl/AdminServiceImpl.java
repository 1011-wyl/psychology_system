package com.cykj.psychology.service.impl;

import com.cykj.psychology.entity.*;
import com.cykj.psychology.mapper.*;
import com.cykj.psychology.service.AdminService;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private SysAdminMapper adminMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysCounselorMapper counselorMapper;

    @Autowired
    private AppointmentRecordMapper appointmentMapper;

    @Autowired
    private AppointmentTimeConfigMapper timeConfigMapper;

    @Autowired
    private AccountTransactionMapper transactionMapper;

    @Autowired
    private PasswordUtils passwordUtils;

    @Override
    public SysAdmin login(String username, String password) {
        SysAdmin admin = adminMapper.selectByUsername(username);
        if (admin != null
                && Integer.valueOf(1).equals(admin.getStatus())
                && passwordUtils.matches(password, admin.getPassword())) {
            if (passwordUtils.needsUpgrade(admin.getPassword())) {
                String upgradedPassword = passwordUtils.encode(password);
                adminMapper.updatePassword(admin.getId(), upgradedPassword);
                admin.setPassword(upgradedPassword);
            }
            return admin;
        }
        return null;
    }

    @Override
    public List<SysUser> getUserList() {
        return adminMapper.selectAllUsers();
    }

    @Override
    public int getUserCount() {
        return adminMapper.countUsers();
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        if (adminMapper.updateUserStatus(userId, status) != 1) {
            throw ApiException.notFound("用户不存在");
        }
    }

    @Override
    public String resetUserPassword(Long userId) {
        String temporaryPassword = passwordUtils.generateTemporaryPassword();
        if (adminMapper.resetUserPassword(userId, passwordUtils.encode(temporaryPassword)) != 1) {
            throw ApiException.notFound("用户不存在");
        }
        return temporaryPassword;
    }

    @Override
    public List<SysCounselor> getCounselorList() {
        return adminMapper.selectAllCounselors();
    }

    @Override
    public String addCounselor(SysCounselor counselor) {
        if (counselorMapper.selectByUsername(counselor.getUsername()) != null) {
            throw ApiException.conflict("咨询师用户名已存在");
        }
        String temporaryPassword = passwordUtils.generateTemporaryPassword();
        counselor.setPassword(passwordUtils.encode(temporaryPassword));
        if (adminMapper.insertCounselor(counselor) != 1) {
            throw ApiException.conflict("添加咨询师失败");
        }
        return temporaryPassword;
    }

    @Override
    public void updateCounselor(SysCounselor counselor) {
        if (adminMapper.updateCounselor(counselor) != 1) {
            throw ApiException.notFound("咨询师不存在");
        }
    }

    @Override
    public void deleteCounselor(Long id) {
        if (adminMapper.deleteCounselor(id) != 1) {
            throw ApiException.notFound("咨询师不存在");
        }
    }

    @Override
    public List<AppointmentRecord> getAppointmentList() {
        return adminMapper.selectAllAppointments();
    }

    @Override
    @Transactional
    public void terminateAppointment(Long id) {
        AppointmentRecord record = appointmentMapper.selectById(id);
        if (record == null) {
            throw ApiException.notFound("预约不存在");
        }
        if (record.getStatus() != 0 && record.getStatus() != 2) {
            throw ApiException.conflict("当前预约状态不能终止");
        }
        if (adminMapper.terminateAppointment(id) != 1) {
            throw ApiException.conflict("预约状态已发生变化，请刷新后重试");
        }

        timeConfigMapper.releaseSlot(
                record.getCounselorId(),
                record.getAppointmentDate(),
                record.getAppointmentTime()
        );

        if (record.getStatus() == 2) {
            if (counselorMapper.deductBalanceIfSufficient(record.getCounselorId(), record.getFee()) != 1) {
                throw ApiException.conflict("咨询师余额异常，无法完成退款");
            }
            if (userMapper.increaseBalance(record.getUserId(), record.getFee()) != 1) {
                throw ApiException.notFound("预约用户不存在");
            }
            SysUser user = userMapper.selectById(record.getUserId());

            AccountTransaction refund = new AccountTransaction();
            refund.setUserId(record.getUserId());
            refund.setRelatedId(record.getId());
            refund.setRelatedType("appointment_refund");
            refund.setTransactionType(0);
            refund.setAmount(record.getFee());
            refund.setBalanceAfter(user.getBalance());
            refund.setDescription("预约终止退款");
            transactionMapper.insert(refund);
        }
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", adminMapper.countUsers());
        stats.put("counselorCount", adminMapper.selectAllCounselors().size());
        stats.put("appointmentCount", adminMapper.selectAllAppointments().size());
        return stats;
    }
}
