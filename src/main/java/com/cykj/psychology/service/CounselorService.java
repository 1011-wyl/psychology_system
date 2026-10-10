package com.cykj.psychology.service;

import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.AppointmentTimeConfig;
import com.cykj.psychology.entity.SysCounselor;
import java.util.List;

public interface CounselorService {

    SysCounselor login(String username, String password);

    List<AppointmentRecord> getAppointments(Long counselorId);

    AppointmentRecord getAppointment(Long counselorId, Long appointmentId);

    void confirmAppointment(Long counselorId, Long appointmentId);

    void diagnoseAppointment(Long counselorId, Long appointmentId, String reply);

    List<AppointmentTimeConfig> getTimeConfig(Long counselorId, String date);

    void saveTimeConfig(Long counselorId, String date, List<String> timeSlots);

    SysCounselor getCounselorById(Long id);
}
