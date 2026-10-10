package com.cykj.psychology.service;

import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.mapper.EvaluateFieldMapper;
import com.cykj.psychology.mapper.EvaluateQuestionMapper;
import com.cykj.psychology.mapper.EvaluateReportMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.mapper.SystemParamMapper;
import com.cykj.psychology.service.impl.EvaluateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluateServiceImplTest {
    @Mock
    private EvaluateFieldMapper fieldMapper;
    @Mock
    private EvaluateQuestionMapper questionMapper;
    @Mock
    private EvaluateReportMapper reportMapper;
    @Mock
    private SystemParamMapper paramMapper;
    @Mock
    private SysUserMapper userMapper;
    @Mock
    private AccountTransactionMapper transactionMapper;
    @InjectMocks
    private EvaluateServiceImpl service;

    @Test
    void cannotReadAnotherUsersReport() {
        when(reportMapper.selectByIdAndUserId(8L, 3L)).thenReturn(null);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.getReportDetail(3L, 8L)
        );

        assertEquals(404, exception.getStatus());
    }
}
