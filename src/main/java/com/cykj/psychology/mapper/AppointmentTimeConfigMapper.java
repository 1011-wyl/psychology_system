package com.cykj.psychology.mapper;

import com.cykj.psychology.entity.AppointmentTimeConfig;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AppointmentTimeConfigMapper {

    @Select("SELECT * FROM appointment_time_config WHERE counselor_id = #{counselorId} AND config_date = #{date}")
    List<AppointmentTimeConfig> selectByCounselorAndDate(@Param("counselorId") Long counselorId,
                                                         @Param("date") String date);

    @Delete("DELETE FROM appointment_time_config WHERE counselor_id = #{counselorId} AND config_date = #{date}")
    int deleteByDate(@Param("counselorId") Long counselorId, @Param("date") String date);

    @Insert("INSERT INTO appointment_time_config(counselor_id, config_date, time_slot, is_booked, user_id, create_time) " +
            "VALUES(#{counselorId}, #{configDate}, #{timeSlot}, #{isBooked}, #{userId}, NOW())")
    int insert(AppointmentTimeConfig config);

    @Update("UPDATE appointment_time_config SET is_booked = 1, user_id = #{userId} " +
            "WHERE counselor_id = #{counselorId} AND config_date = #{date} " +
            "AND time_slot = #{timeSlot} AND is_booked = 0")
    int reserveSlot(@Param("counselorId") Long counselorId,
                    @Param("date") String date,
                    @Param("timeSlot") String timeSlot,
                    @Param("userId") Long userId);

    @Update("UPDATE appointment_time_config SET is_booked = 0, user_id = NULL " +
            "WHERE counselor_id = #{counselorId} AND config_date = #{date} AND time_slot = #{timeSlot}")
    int releaseSlot(@Param("counselorId") Long counselorId,
                    @Param("date") java.util.Date date,
                    @Param("timeSlot") String timeSlot);
}
