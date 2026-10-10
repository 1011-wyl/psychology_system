package com.cykj.psychology.dto;

import com.cykj.psychology.entity.SysCounselor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CounselorPublicDto {
    private Long id;
    private String realName;
    private String title;
    private String avatar;
    private BigDecimal fee;
    private String description;
    private BigDecimal rating;

    public static CounselorPublicDto from(SysCounselor counselor) {
        CounselorPublicDto result = new CounselorPublicDto();
        result.setId(counselor.getId());
        result.setRealName(counselor.getRealName());
        result.setTitle(counselor.getTitle());
        result.setAvatar(counselor.getAvatar());
        result.setFee(counselor.getFee());
        result.setDescription(counselor.getDescription());
        result.setRating(counselor.getRating());
        return result;
    }
}
