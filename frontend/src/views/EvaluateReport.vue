<template>
  <div class="report-container">
    <el-card class="report-card" shadow="hover">
      <div class="report-header">
        <i class="el-icon-document-checked"></i>
        <h2>心理评估报告</h2>
      </div>

      <div class="report-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评估领域">{{ fieldName }}</el-descriptions-item>
          <el-descriptions-item label="评估时间">{{ reportTime }}</el-descriptions-item>
          <el-descriptions-item label="测评总分">
            <span :class="scoreClass">{{ totalScore }} 分</span>
          </el-descriptions-item>
          <el-descriptions-item label="评估结论">
            <span :class="conclusionClass">{{ conclusion }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="report-content">
        <h3>报告内容</h3>
        <p>{{ reportContent }}</p>
      </div>

      <div class="report-footer">
        <el-button type="primary" plain @click="goToEvaluate">继续评估</el-button>
        <el-button type="success" @click="goToReports">查看全部报告</el-button>
        <el-button @click="goToHome">返回首页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'EvaluateReport',
  data() {
    return {
      reportId: null,
      fieldName: '',
      totalScore: 0,
      conclusion: '',
      reportTime: ''
    }
  },
  computed: {
    scoreClass() {
      if (this.totalScore <= 10) return 'score-good'
      if (this.totalScore <= 15) return 'score-warning'
      return 'score-danger'
    },
    conclusionClass() {
      if (this.conclusion.includes('良好')) return 'conclusion-good'
      if (this.conclusion.includes('轻度')) return 'conclusion-warning'
      if (this.conclusion.includes('中度')) return 'conclusion-danger'
      return 'conclusion-severe'
    },
    reportContent() {
      if (this.totalScore <= 10) {
        return '您的心理健康状况良好，请继续保持积极乐观的生活态度。建议定期进行自我评估，关注情绪变化。'
      } else if (this.totalScore <= 15) {
        return '您目前可能存在一定的心理困扰。建议您适当调整作息，增加运动，与亲友多沟通。如果问题持续，可以考虑寻求专业心理咨询。'
      } else if (this.totalScore <= 20) {
        return '您的心理状况需要关注。建议您尽快预约专业心理咨询师进行深入评估，学习压力管理和情绪调节技巧。'
      } else {
        return '您的心理状况需要及时干预。强烈建议您尽快预约专业心理咨询师或前往医院心理科就诊，获取专业的帮助和支持。'
      }
    }
  },
  async created() {
  this.reportId = this.$route.query.reportId
  this.fieldName = this.$route.query.fieldName || '心理评估'
  
  if (this.reportId) {
    try {
      const response = await axios.get(`evaluate/report/${this.reportId}`)
      if (response.data.code === 200) {
        const report = response.data.data
        this.totalScore = report.totalScore
        this.conclusion = report.conclusion
        this.reportTime = this.formatDate(new Date(report.createTime))
      }
    } catch (error) {
      console.error('获取报告失败:', error)
    }
  } else {
    // 从 query 参数获取
    this.totalScore = parseInt(this.$route.query.totalScore) || 0
    this.conclusion = this.$route.query.conclusion || '评估完成'
    this.reportTime = this.formatDate(new Date())
    }
  },
  methods: {
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    goToEvaluate() {
      this.$router.push('/evaluate')
    },
    goToReports() {
      this.$router.push('/report')
    },
    goToHome() {
      this.$router.push('/home')
    }
  }
}
</script>

<style scoped>
.report-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.report-card {
  width: 800px;
  max-width: 90%;
  border-radius: 16px;
}

.report-header {
  text-align: center;
  padding: 20px 0;
  border-bottom: 2px solid #e4e7ed;
}

.report-header i {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 10px;
}

.report-header h2 {
  margin: 0;
  color: #333;
}

.report-info {
  padding: 20px;
}

.score-good {
  color: #67c23a;
  font-weight: bold;
  font-size: 18px;
}

.score-warning {
  color: #e6a23c;
  font-weight: bold;
  font-size: 18px;
}

.score-danger {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.conclusion-good {
  color: #67c23a;
  font-weight: bold;
}

.conclusion-warning {
  color: #e6a23c;
  font-weight: bold;
}

.conclusion-danger {
  color: #f56c6c;
  font-weight: bold;
}

.conclusion-severe {
  color: #ff0000;
  font-weight: bold;
}

.report-content {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin: 0 20px 20px;
}

.report-content h3 {
  margin-top: 0;
  color: #333;
}

.report-content p {
  line-height: 1.8;
  color: #606266;
}

.report-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>
