package com.cykj.psychology.service;

import com.cykj.psychology.entity.EvaluateField;
import com.cykj.psychology.entity.EvaluateQuestion;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Test
    void negativeEvaluationFeeCannotIncreaseUserBalance() {
        EvaluateField field = new EvaluateField();
        field.setStatus(1);
        EvaluateQuestion question = new EvaluateQuestion();
        question.setId(9L);
        question.setScoreA(1);
        when(fieldMapper.selectById(2L)).thenReturn(field);
        when(questionMapper.selectByFieldId(2L)).thenReturn(Collections.singletonList(question));
        when(paramMapper.getValueByKey("在线评估费用")).thenReturn("-10.00");

        assertThrows(
                IllegalStateException.class,
                () -> service.submitEvaluation(3L, 2L, Collections.singletonMap("9", "A"))
        );

        verify(userMapper, never()).deductBalanceIfSufficient(any(), any());
        verify(reportMapper, never()).insert(any());
    }
}
