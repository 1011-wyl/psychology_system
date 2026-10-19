package com.cykj.psychology.service;

import com.cykj.psychology.entity.EvaluateField;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AppointmentRecordMapper;
import com.cykj.psychology.mapper.AppointmentTimeConfigMapper;
import com.cykj.psychology.mapper.EvaluateFieldMapper;
import com.cykj.psychology.mapper.SysCounselorMapper;
import com.cykj.psychology.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
    @Mock
    private SysCounselorMapper counselorMapper;
    @Mock
    private AppointmentRecordMapper appointmentMapper;
    @Mock
    private AppointmentTimeConfigMapper timeConfigMapper;
    @Mock
    private EvaluateFieldMapper fieldMapper;
    @InjectMocks
    private AppointmentServiceImpl service;

    @Test
    void refusesBookingWhenAtomicSlotReservationLosesRace() {
        SysCounselor counselor = new SysCounselor();
        counselor.setStatus(1);
        counselor.setFee(new BigDecimal("100.00"));
        when(counselorMapper.selectById(2L)).thenReturn(counselor);
        EvaluateField field = new EvaluateField();
        field.setStatus(1);
        when(fieldMapper.selectById(4L)).thenReturn(field);
        when(appointmentMapper.selectByCounselorAndDate(2L, "2099-01-01"))
                .thenReturn(Collections.emptyList());
        when(timeConfigMapper.reserveSlot(2L, "2099-01-01", "09:00-10:00", 1L))
                .thenReturn(0);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.createAppointment(
                        1L, 2L, 4L, "2099-01-01", "09:00-10:00", "test"
                )
        );

        assertEquals(409, exception.getStatus());
        verify(appointmentMapper, never()).insert(any());
    }
}
