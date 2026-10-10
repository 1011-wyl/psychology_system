package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.EvaluateReport;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface EvaluateReportMapper {

    int insert(EvaluateReport report);

    List<EvaluateReport> selectByUserId(Long userId);

    EvaluateReport selectById(Long id);

    EvaluateReport selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
