package com.cykj.psychology.controller;

import com.cykj.psychology.common.Result;
import com.cykj.psychology.dto.EvaluateQuestionDto;
import com.cykj.psychology.dto.request.EvaluationSubmitRequest;
import com.cykj.psychology.entity.EvaluateField;
import com.cykj.psychology.entity.EvaluateQuestion;
import com.cykj.psychology.entity.EvaluateReport;
import com.cykj.psychology.service.EvaluateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {
    private final EvaluateService evaluateService;

    public EvaluateController(EvaluateService evaluateService) {
        this.evaluateService = evaluateService;
    }

    @GetMapping("/fields")
    public Result<List<EvaluateField>> getFields() {
        return Result.success(evaluateService.getFields());
    }

    @GetMapping("/questions/{fieldId}")
    public Result<List<EvaluateQuestionDto>> getQuestions(@PathVariable Long fieldId) {
        return Result.success(evaluateService.getQuestions(fieldId).stream()
                .map(EvaluateQuestionDto::from)
                .collect(Collectors.toList()));
    }

    @PostMapping("/submit")
    public Result<EvaluateReport> submit(
            @Valid @RequestBody EvaluationSubmitRequest body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(evaluateService.submitEvaluation(
                userId,
                body.getFieldId(),
                body.getAnswers()
        ));
    }

    @GetMapping("/reports")
    public Result<List<EvaluateReport>> getReports(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(evaluateService.getUserReports(userId));
    }

    @GetMapping("/report/{id}")
    public Result<EvaluateReport> getReportDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(evaluateService.getReportDetail(userId, id));
    }
}
