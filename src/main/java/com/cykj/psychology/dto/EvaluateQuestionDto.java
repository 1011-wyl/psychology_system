package com.cykj.psychology.dto;

import com.cykj.psychology.entity.EvaluateQuestion;
import lombok.Data;

@Data
public class EvaluateQuestionDto {
    private Long id;
    private Long fieldId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer sortOrder;

    public static EvaluateQuestionDto from(EvaluateQuestion question) {
        EvaluateQuestionDto result = new EvaluateQuestionDto();
        result.setId(question.getId());
        result.setFieldId(question.getFieldId());
        result.setQuestionText(question.getQuestionText());
        result.setOptionA(question.getOptionA());
        result.setOptionB(question.getOptionB());
        result.setOptionC(question.getOptionC());
        result.setOptionD(question.getOptionD());
        result.setSortOrder(question.getSortOrder());
        return result;
    }
}
