<template>
  <PortalShell role="user">
    <div class="detail-container">
      <el-card class="detail-card" shadow="hover" v-loading="loading">
        <div class="detail-header">
          <i class="el-icon-document-checked"></i>
          <h2>心理评估报告详情</h2>
        </div>

        <el-descriptions :column="2" border v-if="report">
          <el-descriptions-item label="评估领域">{{
            fieldMap[report.fieldId] || report.fieldName || '心理评估'
          }}</el-descriptions-item>
          <el-descriptions-item label="评估时间">{{
            formatDate(report.createTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="测评总分">
            <span :class="scoreClass">{{ report.totalScore }} 分</span>
          </el-descriptions-item>
          <el-descriptions-item label="评估结论">
            <span :class="conclusionClass">{{ report.conclusion }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="扣除费用"
            >¥ {{ report.fee }}</el-descriptions-item
          >
        </el-descriptions>

        <div v-if="report" class="report-content">
          <h3>报告内容</h3>
          <p>{{ report.reportContent }}</p>
        </div>
        <el-empty
          v-if="!loading && !report"
          description="暂时无法读取这份报告"
        ></el-empty>

        <div class="detail-footer">
          <el-button type="primary" plain @click="goToEvaluate"
            >继续评估</el-button
          >
          <el-button @click="goBack">返回</el-button>
        </div>
      </el-card>
    </div>
  </PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
import axios from '@/api/http'

export default {
  name: 'ReportDetail',
  components: { PortalShell },
  data() {
    return {
      report: null,
      loading: true,
      fieldMap: {},
    }
  },
  computed: {
    scoreClass() {
      if (!this.report) return ''
      const score = this.report.totalScore
      if (score <= 10) return 'score-good'
      if (score <= 15) return 'score-warning'
      if (score <= 20) return 'score-danger'
      return 'score-severe'
    },
    conclusionClass() {
      if (!this.report) return ''
      const score = this.report.totalScore
      if (score <= 10) return 'conclusion-good'
      if (score <= 15) return 'conclusion-warning'
      if (score <= 20) return 'conclusion-danger'
      return 'conclusion-severe'
    },
  },
  created() {
    this.fetchFields()
    this.fetchReportDetail()
  },
  methods: {
    async fetchFields() {
      try {
        const res = await axios.get('/evaluate/fields')
        if (res.data.code === 200) {
          const map = {}
          res.data.data.forEach((field) => {
            map[field.id] = field.fieldName
          })
          this.fieldMap = map
        }
      } catch (error) {
        console.error('获取领域失败:', error)
      }
    },
    async fetchReportDetail() {
      const reportId = this.$route.params.id
      try {
        const res = await axios.get(`/evaluate/report/${reportId}`)
        if (res.data.code === 200) {
          this.report = res.data.data
        }
      } catch (error) {
        console.error('获取报告失败:', error)
        this.$message.error('加载报告失败')
      } finally {
        this.loading = false
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
        2,
        '0'
      )}-${String(date.getDate()).padStart(2, '0')} ${String(
        date.getHours()
      ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    goToEvaluate() {
      this.$router.push('/evaluate')
    },
    goBack() {
      this.$router.back()
    },
  },
}
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.detail-card {
  width: 800px;
  max-width: 90%;
  border-radius: 16px;
}

.detail-header {
  text-align: center;
  padding: 20px 0;
  border-bottom: 2px solid #e4e7ed;
}

.detail-header i {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 10px;
}

.detail-header h2 {
  margin: 0;
  color: #333;
}

.report-content {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin: 20px;
}

.report-content h3 {
  margin-top: 0;
  color: #333;
}

.report-content p {
  line-height: 1.8;
  color: #606266;
}

.detail-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
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
.score-severe {
  color: #ff0000;
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
</style>
