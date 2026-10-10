package com.cykj.psychology.service;

import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.SysCounselor;
import java.util.List;

public interface AppointmentService {

    List<SysCounselor> getCounselors();

    SysCounselor getCounselorById(Long id);

    AppointmentRecord createAppointment(Long userId, Long counselorId, Long fieldId,
                                        String date, String timeSlot, String problemDesc);

    List<AppointmentRecord> getUserAppointments(Long userId);

    List<String> getAvailableTimeSlots(Long counselorId, String date);

    AppointmentRecord getAppointmentById(Long id);
}