package com.cykj.psychology.service;

import com.cykj.psychology.entity.EvaluateField;
import com.cykj.psychology.entity.EvaluateQuestion;
import com.cykj.psychology.entity.EvaluateReport;
import java.util.List;
import java.util.Map;

public interface EvaluateService {
    List<EvaluateField> getFields();
    List<EvaluateQuestion> getQuestions(Long fieldId);
    EvaluateReport submitEvaluation(Long userId, Long fieldId, Map<String, String> answers);
    List<EvaluateReport> getUserReports(Long userId);
    EvaluateReport getReportDetail(Long userId, Long reportId);
}
