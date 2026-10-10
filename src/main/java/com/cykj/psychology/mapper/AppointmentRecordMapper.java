package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.AppointmentRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AppointmentRecordMapper {

    // 新增预约
    @Insert("INSERT INTO appointment_record(user_id, counselor_id, field_id, appointment_date, appointment_time, problem_desc, status, fee, create_time) " +
            "VALUES(#{userId}, #{counselorId}, #{fieldId}, #{appointmentDate}, #{appointmentTime}, #{problemDesc}, 0, #{fee}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppointmentRecord record);

    // 查询用户的所有预约
    @Select("SELECT * FROM appointment_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<AppointmentRecord> selectByUserId(Long userId);

    // 查询咨询师某一天的预约（用于判断时间段是否已被预约）
    @Select("SELECT * FROM appointment_record WHERE counselor_id = #{counselorId} AND appointment_date = #{date}")
    List<AppointmentRecord> selectByCounselorAndDate(@Param("counselorId") Long counselorId,
                                                     @Param("date") String date);

    // 查询咨询师的预约列表
    @Select("SELECT * FROM appointment_record WHERE counselor_id = #{counselorId} ORDER BY appointment_date DESC, appointment_time DESC")
    List<AppointmentRecord> selectByCounselorId(Long counselorId);

    // 确认预约
    @Update("UPDATE appointment_record SET status = 2, confirm_time = NOW() " +
            "WHERE id = #{id} AND counselor_id = #{counselorId} AND status = 0")
    int confirmIfPending(@Param("id") Long id, @Param("counselorId") Long counselorId);

    // 诊断答复
    @Update("UPDATE appointment_record SET status = 3, diagnosis_reply = #{reply}, reply_time = NOW() " +
            "WHERE id = #{id} AND counselor_id = #{counselorId} AND status = 2")
    int diagnoseIfConfirmed(@Param("id") Long id,
                            @Param("counselorId") Long counselorId,
                            @Param("reply") String reply);

    @Select("SELECT * FROM appointment_record WHERE id = #{id}")
    AppointmentRecord selectById(Long id);

    @Select("SELECT * FROM appointment_record WHERE id = #{id} AND counselor_id = #{counselorId}")
    AppointmentRecord selectByIdAndCounselor(@Param("id") Long id,
                                             @Param("counselorId") Long counselorId);

    // 更新预约记录
    @Update("UPDATE appointment_record SET status=#{status}, confirm_time=#{confirmTime}, " +
            "diagnosis_reply=#{diagnosisReply}, reply_time=#{replyTime}, " +
            "evaluation=#{evaluation}, evaluation_time=#{evaluationTime} WHERE id=#{id}")
    int update(AppointmentRecord record);
}
