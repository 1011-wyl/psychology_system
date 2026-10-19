package com.cykj.psychology.service.impl;

import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import com.cykj.psychology.entity.AccountTransaction;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.utils.PasswordUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private AccountTransactionMapper transactionMapper;

    @Autowired
    private PasswordUtils passwordUtils;

    @Override
    public List<SysUser> findAll() {
        return sysUserMapper.selectAll();
    }

    @Override
    public SysUser findById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public boolean register(SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return false;  // 用户名已存在
        }
        // 密码加密存储
        user.setPassword(passwordUtils.encode(user.getPassword()));
        // 设置默认值
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(1);  // 启用状态

        // 插入数据库
        try {
            return sysUserMapper.insert(user) > 0;
        } catch (DuplicateKeyException exception) {
            return false;
        }
    }

    @Override
    public SysUser login(String username, String password) {
        SysUser user = sysUserMapper.selectByUsername(username);
        // 验证用户名、密码、状态
        if (user != null
                && Integer.valueOf(1).equals(user.getStatus())
                && passwordUtils.matches(password, user.getPassword())) {
            if (passwordUtils.needsUpgrade(user.getPassword())) {
                String upgradedPassword = passwordUtils.encode(password);
                sysUserMapper.updatePassword(user.getId(), upgradedPassword);
                user.setPassword(upgradedPassword);
            }
            return user;
        }
        return null;
    }

    @Override
    public boolean update(SysUser user) {
        int result = sysUserMapper.update(user);
        return result > 0;
    }

    @Override
    @Transactional
    public SysUser recharge(Long userId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw ApiException.badRequest("充值金额必须大于0");
        }
        if (sysUserMapper.increaseBalance(userId, amount) != 1) {
            throw ApiException.notFound("用户不存在");
        }

        SysUser user = sysUserMapper.selectById(userId);
        AccountTransaction transaction = new AccountTransaction();
        transaction.setUserId(userId);
        transaction.setRelatedType("recharge");
        transaction.setTransactionType(0);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(user.getBalance());
        transaction.setDescription("账户充值");
        transactionMapper.insert(transaction);

        return user;
    }
}
