package com.cykj.psychology.service.impl;

import com.cykj.psychology.entity.AccountTransaction;
import com.cykj.psychology.entity.EvaluateField;
import com.cykj.psychology.entity.EvaluateQuestion;
import com.cykj.psychology.entity.EvaluateReport;
import com.cykj.psychology.entity.SysUser;
import com.cykj.psychology.exception.ApiException;
import com.cykj.psychology.mapper.AccountTransactionMapper;
import com.cykj.psychology.mapper.EvaluateFieldMapper;
import com.cykj.psychology.mapper.EvaluateQuestionMapper;
import com.cykj.psychology.mapper.EvaluateReportMapper;
import com.cykj.psychology.mapper.SysUserMapper;
import com.cykj.psychology.mapper.SystemParamMapper;
import com.cykj.psychology.service.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class EvaluateServiceImpl implements EvaluateService {

    @Autowired
    private EvaluateFieldMapper fieldMapper;

    @Autowired
    private EvaluateQuestionMapper questionMapper;

    @Autowired
    private EvaluateReportMapper reportMapper;

    @Autowired
    private SystemParamMapper paramMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private AccountTransactionMapper transactionMapper;

    @Override
    public List<EvaluateField> getFields() {
        return fieldMapper.selectAll();
    }

    @Override
    public List<EvaluateQuestion> getQuestions(Long fieldId) {
        return questionMapper.selectByFieldId(fieldId);
    }

    @Override
    @Transactional
    public EvaluateReport submitEvaluation(Long userId, Long fieldId, Map<String, String> answers) {
        EvaluateField field = fieldMapper.selectById(fieldId);
        if (field == null || field.getStatus() != 1) {
            throw ApiException.badRequest("评估领域不存在或已停用");
        }

        List<EvaluateQuestion> questions = questionMapper.selectByFieldId(fieldId);
        if (questions.isEmpty()) {
            throw ApiException.badRequest("该领域暂时没有可用题目");
        }
        if (answers.size() != questions.size()) {
            throw ApiException.badRequest("请完成全部评估题目");
        }

        int totalScore = 0;
        for (EvaluateQuestion question : questions) {
            String answer = answers.get(String.valueOf(question.getId()));
            if (answer == null) {
                throw ApiException.badRequest("请完成全部评估题目");
            }
            switch (answer) {
                case "A":
                    totalScore += question.getScoreA();
                    break;
                case "B":
                    totalScore += question.getScoreB();
                    break;
                case "C":
                    totalScore += question.getScoreC();
                    break;
                case "D":
                    totalScore += question.getScoreD();
                    break;
                default:
                    throw ApiException.badRequest("评估答案包含无效选项");
            }
        }

        String feeValue = paramMapper.getValueByKey("在线评估费用");
        BigDecimal fee;
        try {
            fee = new BigDecimal(feeValue == null ? "10" : feeValue);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("在线评估费用配置不正确", exception);
        }
        if (fee.signum() < 0) {
            throw new IllegalStateException("在线评估费用不能为负数");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw ApiException.notFound("用户不存在");
        }
        if (userMapper.deductBalanceIfSufficient(userId, fee) != 1) {
            throw ApiException.conflict("余额不足，请先充值");
        }

        EvaluateReport report = new EvaluateReport();
        report.setUserId(userId);
        report.setFieldId(fieldId);
        report.setTotalScore(totalScore);
        report.setConclusion(getConclusion(totalScore));
        report.setReportContent(getReportContent(totalScore));
        report.setFee(fee);
        reportMapper.insert(report);

        SysUser updatedUser = userMapper.selectById(userId);
        AccountTransaction transaction = new AccountTransaction();
        transaction.setUserId(userId);
        transaction.setRelatedId(report.getId());
        transaction.setRelatedType("evaluation");
        transaction.setTransactionType(1);
        transaction.setAmount(fee);
        transaction.setBalanceAfter(updatedUser.getBalance());
        transaction.setDescription("在线心理评估");
        transactionMapper.insert(transaction);
        return report;
    }

    @Override
    public List<EvaluateReport> getUserReports(Long userId) {
        return reportMapper.selectByUserId(userId);
    }

    @Override
    public EvaluateReport getReportDetail(Long userId, Long reportId) {
        EvaluateReport report = reportMapper.selectByIdAndUserId(reportId, userId);
        if (report == null) {
            throw ApiException.notFound("报告不存在");
        }
        return report;
    }

    private String getConclusion(int score) {
        if (score <= 10) {
            return "心理健康状况良好";
        }
        if (score <= 15) {
            return "轻度心理困扰";
        }
        if (score <= 20) {
            return "中度心理困扰";
        }
        return "重度心理困扰，建议寻求专业帮助";
    }

    private String getReportContent(int score) {
        if (score <= 10) {
            return "您的心理健康状况良好，请继续保持积极乐观的生活态度。"
                    + "建议定期进行自我评估，关注情绪变化。";
        }
        if (score <= 15) {
            return "您目前可能存在一定的心理困扰。建议适当调整作息、增加运动并与亲友沟通。"
                    + "如果问题持续，可以考虑寻求专业心理咨询。";
        }
        return "您的心理状态需要关注。建议尽快预约专业心理咨询师进行深入评估，"
                + "学习压力管理和情绪调节技巧。";
    }
}
