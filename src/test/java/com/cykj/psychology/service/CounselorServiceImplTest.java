package com.cykj.psychology.service;

import com.cykj.psychology.entity.AppointmentRecord;
import com.cykj.psychology.entity.SysCounselor;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.mapper.AppointmentRecordMapper;
import com.cykj.psychology.mapper.AppointmentTimeConfigMapper;
import com.cykj.psychology.mapper.SysCounselorMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.service.impl.CounselorServiceImpl;
import com.cykj.psychology.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CounselorServiceImplTest {
    @Mock
    private SysCounselorMapper counselorMapper;
    @Mock
    private AppointmentRecordMapper appointmentMapper;
    @Mock
    private AppointmentTimeConfigMapper timeConfigMapper;
    @Mock
    private SysUserMapper userMapper;
    @Mock
    private AccountTransactionMapper transactionMapper;
    @Mock
    private PasswordUtils passwordUtils;
    @InjectMocks
    private CounselorServiceImpl service;

    @Test
    void cannotConfirmAnotherCounselorsAppointment() {
        when(appointmentMapper.selectByIdAndCounselor(99L, 5L)).thenReturn(null);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.confirmAppointment(5L, 99L)
        );

        assertEquals(404, exception.getStatus());
        verify(appointmentMapper, never()).confirmIfPending(any(), any());
        verify(userMapper, never()).deductBalanceIfSufficient(any(), any());
    }

    @Test
    void confirmsOwnedPendingAppointmentWithAtomicBalanceUpdates() {
        AppointmentRecord record = new AppointmentRecord();
        record.setId(10L);
        record.setCounselorId(5L);
        record.setUserId(3L);
        record.setStatus(0);
        record.setFee(new BigDecimal("50.00"));
        when(appointmentMapper.selectByIdAndCounselor(10L, 5L)).thenReturn(record);
        when(appointmentMapper.confirmIfPending(10L, 5L)).thenReturn(1);
        when(userMapper.deductBalanceIfSufficient(3L, record.getFee())).thenReturn(1);
        when(counselorMapper.increaseBalance(5L, record.getFee())).thenReturn(1);

        SysUser user = new SysUser();
        user.setBalance(new BigDecimal("50.00"));
        when(userMapper.selectById(3L)).thenReturn(user);
        SysCounselor counselor = new SysCounselor();
        counselor.setRealName("测试咨询师");
        when(counselorMapper.selectById(5L)).thenReturn(counselor);

        service.confirmAppointment(5L, 10L);

        verify(appointmentMapper).confirmIfPending(10L, 5L);
        verify(userMapper).deductBalanceIfSufficient(3L, new BigDecimal("50.00"));
        verify(counselorMapper).increaseBalance(5L, new BigDecimal("50.00"));
        verify(transactionMapper).insert(any());
    }
}
