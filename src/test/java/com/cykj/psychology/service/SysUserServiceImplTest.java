package com.cykj.psychology.service;

import com.cykj.psychology.entity.AccountTransaction;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.service.impl.SysUserServiceImpl;
import com.cykj.psychology.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {
    @Mock
    private SysUserMapper userMapper;
    @Mock
    private AccountTransactionMapper transactionMapper;
    @Mock
    private PasswordUtils passwordUtils;
    @InjectMocks
    private SysUserServiceImpl service;

    @Test
    void simulatedRechargeUpdatesBalanceAndCreatesLedgerEntry() {
        Long userId = 7L;
        BigDecimal amount = new BigDecimal("25.50");
        SysUser updatedUser = new SysUser();
        updatedUser.setId(userId);
        updatedUser.setBalance(new BigDecimal("125.50"));

        when(userMapper.increaseBalance(userId, amount)).thenReturn(1);
        when(userMapper.selectById(userId)).thenReturn(updatedUser);

        SysUser result = service.recharge(userId, amount);

        assertEquals(updatedUser, result);
        verify(userMapper).increaseBalance(userId, amount);
        ArgumentCaptor<AccountTransaction> captor = ArgumentCaptor.forClass(AccountTransaction.class);
        verify(transactionMapper).insert(captor.capture());
        AccountTransaction transaction = captor.getValue();
        assertEquals(userId, transaction.getUserId());
        assertEquals("recharge", transaction.getRelatedType());
        assertEquals(0, transaction.getTransactionType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(updatedUser.getBalance(), transaction.getBalanceAfter());
    }
}
