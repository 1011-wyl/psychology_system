package com.cykj.psychology.service.impl;

import com.cykj.psychology.entity.AccountTransaction;
import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.AppointmentTimeConfig;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.mapper.AppointmentRecordMapper;
import com.cykj.psychology.mapper.AppointmentTimeConfigMapper;
import com.cykj.psychology.mapper.SysCounselorMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.service.CounselorService;
import com.cykj.psychology.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CounselorServiceImpl implements CounselorService {

    @Autowired
    private SysCounselorMapper counselorMapper;

    @Autowired
    private AppointmentRecordMapper appointmentMapper;

    @Autowired
    private AppointmentTimeConfigMapper timeConfigMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private AccountTransactionMapper transactionMapper;

    @Autowired
    private PasswordUtils passwordUtils;

    @Override
    public SysCounselor login(String username, String password) {
        SysCounselor counselor = counselorMapper.selectByUsername(username);
        if (counselor != null
                && Integer.valueOf(1).equals(counselor.getStatus())
                && passwordUtils.matches(password, counselor.getPassword())) {
            if (passwordUtils.needsUpgrade(counselor.getPassword())) {
                String upgradedPassword = passwordUtils.encode(password);
                counselorMapper.updatePassword(counselor.getId(), upgradedPassword);
                counselor.setPassword(upgradedPassword);
            }
            return counselor;
        }
        return null;
    }

    @Override
    public List<AppointmentRecord> getAppointments(Long counselorId) {
        return appointmentMapper.selectByCounselorId(counselorId);
    }

    @Override
    public AppointmentRecord getAppointment(Long counselorId, Long appointmentId) {
        AppointmentRecord record = appointmentMapper.selectByIdAndCounselor(appointmentId, counselorId);
        if (record == null) {
            throw ApiException.notFound("预约不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void confirmAppointment(Long counselorId, Long appointmentId) {
        AppointmentRecord record = getAppointment(counselorId, appointmentId);
        if (record.getStatus() != 0) {
            throw ApiException.conflict("当前预约状态不能确认");
        }
        if (appointmentMapper.confirmIfPending(appointmentId, counselorId) != 1) {
            throw ApiException.conflict("预约状态已发生变化，请刷新后重试");
        }
        if (userMapper.deductBalanceIfSufficient(record.getUserId(), record.getFee()) != 1) {
            throw ApiException.conflict("用户余额不足");
        }
        if (counselorMapper.increaseBalance(counselorId, record.getFee()) != 1) {
            throw ApiException.notFound("咨询师不存在");
        }

        SysUser user = userMapper.selectById(record.getUserId());
        SysCounselor counselor = counselorMapper.selectById(counselorId);
        AccountTransaction transaction = new AccountTransaction();
        transaction.setUserId(record.getUserId());
        transaction.setRelatedId(appointmentId);
        transaction.setRelatedType("appointment");
        transaction.setTransactionType(1);
        transaction.setAmount(record.getFee());
        transaction.setBalanceAfter(user.getBalance());
        transaction.setDescription("预约咨询 - " + counselor.getRealName());
        transactionMapper.insert(transaction);
    }

    @Override
    @Transactional
    public void diagnoseAppointment(Long counselorId, Long appointmentId, String reply) {
        getAppointment(counselorId, appointmentId);
        if (appointmentMapper.diagnoseIfConfirmed(appointmentId, counselorId, reply) != 1) {
            throw ApiException.conflict("只有已确认的预约可以提交诊断");
        }
    }

    @Override
    public List<AppointmentTimeConfig> getTimeConfig(Long counselorId, String date) {
        return timeConfigMapper.selectByCounselorAndDate(counselorId, date);
    }

    @Override
    @Transactional
    public void saveTimeConfig(Long counselorId, String date, List<String> timeSlots) {
        LocalDate configDate;
        try {
            configDate = LocalDate.parse(date);
        } catch (Exception exception) {
            throw ApiException.badRequest("日期格式不正确");
        }
        if (configDate.isBefore(LocalDate.now())) {
            throw ApiException.badRequest("不能配置过去的日期");
        }

        Set<String> uniqueSlots = new LinkedHashSet<>(timeSlots);
        List<AppointmentRecord> activeAppointments = appointmentMapper
                .selectByCounselorAndDate(counselorId, date)
                .stream()
                .filter(item -> item.getStatus() == 0 || item.getStatus() == 2)
                .collect(Collectors.toList());
        for (AppointmentRecord appointment : activeAppointments) {
            if (!uniqueSlots.contains(appointment.getAppointmentTime())) {
                throw ApiException.conflict("已有预约的时间段不能删除");
            }
        }

        timeConfigMapper.deleteByDate(counselorId, date);
        for (String slot : uniqueSlots) {
            AppointmentRecord booked = activeAppointments.stream()
                    .filter(item -> slot.equals(item.getAppointmentTime()))
                    .findFirst()
                    .orElse(null);
            AppointmentTimeConfig config = new AppointmentTimeConfig();
            config.setCounselorId(counselorId);
            config.setConfigDate(Date.valueOf(configDate));
            config.setTimeSlot(slot);
            config.setIsBooked(booked == null ? 0 : 1);
            config.setUserId(booked == null ? null : booked.getUserId());
            timeConfigMapper.insert(config);
        }
    }

    @Override
    public SysCounselor getCounselorById(Long id) {
        SysCounselor counselor = counselorMapper.selectById(id);
        if (counselor == null) {
            throw ApiException.notFound("咨询师不存在");
        }
        return counselor;
    }
}
