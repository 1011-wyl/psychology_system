package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.EvaluateField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface EvaluateFieldMapper {


    List<EvaluateField> selectAll();


    EvaluateField selectById(Long id);
}