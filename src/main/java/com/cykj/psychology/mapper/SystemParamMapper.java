package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.SystemParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemParamMapper {

    String getValueByKey(String paramName);
}