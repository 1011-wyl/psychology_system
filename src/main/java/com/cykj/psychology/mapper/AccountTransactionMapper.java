package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.AccountTransaction;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AccountTransactionMapper {

    @Insert("INSERT INTO account_transaction(user_id, related_id, related_type, transaction_type, amount, balance_after, description, create_time) " +
            "VALUES(#{userId}, #{relatedId}, #{relatedType}, #{transactionType}, #{amount}, #{balanceAfter}, #{description}, NOW())")
    int insert(AccountTransaction transaction);

    @Select("SELECT * FROM account_transaction WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<AccountTransaction> selectByUserId(Long userId);
}