package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.EvaluateQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface EvaluateQuestionMapper {


    List<EvaluateQuestion> selectByFieldId(Long fieldId);
}