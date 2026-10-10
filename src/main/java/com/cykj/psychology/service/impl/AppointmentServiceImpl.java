package com.cykj.psychology.service.impl;

import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.AppointmentTimeConfig;
import com.cykj.psychology.entity.EvaluateField;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AppointmentRecordMapper;
import com.cykj.psychology.mapper.AppointmentTimeConfigMapper;
import com.cykj.psychology.mapper.EvaluateFieldMapper;
import com.cykj.psychology.mapper.SysCounselorMapper;
import com.cykj.psychology.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private SysCounselorMapper counselorMapper;

    @Autowired
    private AppointmentRecordMapper appointmentMapper;

    @Autowired
    private AppointmentTimeConfigMapper timeConfigMapper;

    @Autowired
    private EvaluateFieldMapper fieldMapper;

    @Override
    public List<SysCounselor> getCounselors() {
        return counselorMapper.selectAll();
    }

    @Override
    public SysCounselor getCounselorById(Long id) {
        SysCounselor counselor = counselorMapper.selectById(id);
        if (counselor == null || counselor.getStatus() != 1) {
            throw ApiException.notFound("咨询师不存在");
        }
        return counselor;
    }

    @Override
    public AppointmentRecord getAppointmentById(Long id) {
        return appointmentMapper.selectById(id);
    }

    @Override
    @Transactional
    public AppointmentRecord createAppointment(Long userId, Long counselorId, Long fieldId,
                                               String date, String timeSlot, String problemDesc) {
        SysCounselor counselor = getCounselorById(counselorId);
        EvaluateField field = fieldMapper.selectById(fieldId);
        if (field == null || field.getStatus() != 1) {
            throw ApiException.badRequest("咨询领域不存在或已停用");
        }

        LocalDate appointmentDate;
        try {
            appointmentDate = LocalDate.parse(date);
        } catch (Exception exception) {
            throw ApiException.badRequest("预约日期格式不正确");
        }
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw ApiException.badRequest("不能预约过去的日期");
        }

        boolean alreadyBooked = appointmentMapper.selectByCounselorAndDate(counselorId, date)
                .stream()
                .anyMatch(item -> timeSlot.equals(item.getAppointmentTime())
                        && (item.getStatus() == 0 || item.getStatus() == 2));
        if (alreadyBooked) {
            throw ApiException.conflict("该时间段已被预约");
        }

        if (timeConfigMapper.reserveSlot(counselorId, date, timeSlot, userId) != 1) {
            throw ApiException.conflict("该时间段不可预约或刚刚已被他人预约");
        }

        AppointmentRecord record = new AppointmentRecord();
        record.setUserId(userId);
        record.setCounselorId(counselorId);
        record.setFieldId(fieldId);
        record.setAppointmentDate(Date.valueOf(appointmentDate));
        record.setAppointmentTime(timeSlot);
        record.setProblemDesc(problemDesc.trim());
        record.setFee(counselor.getFee());
        record.setStatus(0);
        appointmentMapper.insert(record);
        return record;
    }

    @Override
    public List<AppointmentRecord> getUserAppointments(Long userId) {
        return appointmentMapper.selectByUserId(userId);
    }

    @Override
    public List<String> getAvailableTimeSlots(Long counselorId, String date) {
        getCounselorById(counselorId);
        Set<String> activeBookings = appointmentMapper.selectByCounselorAndDate(counselorId, date)
                .stream()
                .filter(item -> item.getStatus() == 0 || item.getStatus() == 2)
                .map(AppointmentRecord::getAppointmentTime)
                .collect(Collectors.toSet());

        return timeConfigMapper.selectByCounselorAndDate(counselorId, date)
                .stream()
                .filter(config -> config.getIsBooked() == null || config.getIsBooked() == 0)
                .map(AppointmentTimeConfig::getTimeSlot)
                .filter(slot -> !activeBookings.contains(slot))
                .collect(Collectors.toList());
    }
}
