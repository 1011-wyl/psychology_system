<template>
  <div class="question-container">
    <el-card class="question-card">
      <div slot="header" class="question-header">
        <h2>{{ fieldName }} - 在线测评</h2>
        <div class="progress-info">
          <span>第 {{ currentIndex + 1 }} / {{ questions.length }} 题</span>
          <el-progress :percentage="progressPercent" :show-text="false"></el-progress>
        </div>
      </div>

      <div class="question-content">
        <h3 class="question-text">{{ currentQuestion.questionText }}</h3>
        
        <el-radio-group v-model="selectedOption" class="option-group">
          <el-radio 
            v-for="(option, index) in options" 
            :key="index"
            :label="option.value"
            class="option-item"
          >
            <span class="option-label">{{ option.label }}.</span>
            <span class="option-text">{{ option.text }}</span>
          </el-radio>
        </el-radio-group>
      </div>

      <div class="question-footer">
        <el-button 
          v-if="currentIndex > 0" 
          @click="prevQuestion"
          plain
        >上一题</el-button>
        
        <el-button 
          v-if="currentIndex < questions.length - 1" 
          type="primary" 
          @click="nextQuestion"
          :disabled="!selectedOption"
        >下一题</el-button>
        
        <el-button 
          v-else 
          type="success" 
          @click="submitEvaluation"
          :disabled="!selectedOption"
          :loading="submitting"
        >提交评估</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios, { getErrorMessage } from '@/api/http'

export default {
  name: 'EvaluateQuestion',
  data() {
    return {
      fieldId: null,
      fieldName: '',
      questions: [],
      currentIndex: 0,
      answers: [],
      selectedOption: null,
      submitting: false
    }
  },
  computed: {
    currentQuestion() {
      return this.questions[this.currentIndex] || {}
    },
    progressPercent() {
      return ((this.currentIndex + 1) / this.questions.length) * 100
    },
    options() {
      const q = this.currentQuestion
      if (!q) return []
      return [
        { value: 'A', label: 'A', text: q.optionA },
        { value: 'B', label: 'B', text: q.optionB },
        { value: 'C', label: 'C', text: q.optionC },
        { value: 'D', label: 'D', text: q.optionD }
      ]
    }
  },
  created() {
    this.fieldId = this.$route.query.fieldId
    this.fieldName = this.$route.query.fieldName
    if (!this.fieldId) {
      this.$router.push('/evaluate')
    }
    this.fetchQuestions()
  },
  methods: {
    async fetchQuestions() {
  try {
    const response = await axios.get(`/evaluate/questions/${this.fieldId}`)
    if (response.data.code === 200) {
      this.questions = response.data.data
      this.answers = new Array(this.questions.length).fill(null)
    }
  } catch (error) {
    console.error('获取题目失败:', error)
    this.$message.error('加载题目失败')
  }
},
    nextQuestion() {
      if (this.selectedOption) {
        this.saveAnswer()
        this.currentIndex++
        this.loadAnswer()
      }
    },
    prevQuestion() {
      this.saveAnswer()
      this.currentIndex--
      this.loadAnswer()
    },
    saveAnswer() {
      if (this.selectedOption) {
        const currentQuestion = this.currentQuestion
        this.answers[this.currentIndex] = {
          questionId: currentQuestion.id,
          selectedOption: this.selectedOption
        }
      }
    },
    loadAnswer() {
      const saved = this.answers[this.currentIndex]
      this.selectedOption = saved ? saved.selectedOption : null
    },
    async submitEvaluation() {
  this.saveAnswer()
  
  const unanswered = this.answers.some(a => a === null)
  if (unanswered) {
    this.$message.warning('请完成所有题目')
    return
  }
  
  this.submitting = true
  
  try {
    const answersMap = {}
    this.answers.forEach(a => {
      if (a) {
        answersMap[a.questionId] = a.selectedOption
      }
    })

    const response = await axios.post('/evaluate/submit', {
      fieldId: this.fieldId,
      answers: answersMap
    })
    
    if (response.data.code === 200) {
      const report = response.data.data
      this.$router.push({
        path: '/evaluate/report',
        query: {
          reportId: report.id,
          totalScore: report.totalScore,
          conclusion: report.conclusion,
          fieldName: this.fieldName
        }
      })
    } else {
      this.$message.error(response.data.msg)
    }
  } catch (error) {
    console.error('提交评估失败:', error)
    this.$message.error(getErrorMessage(error, '提交失败，请稍后重试'))
  } finally {
    this.submitting = false
  }

    }
  }
}
</script>

<style scoped>
.question-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.question-card {
  width: 800px;
  max-width: 90%;
  border-radius: 16px;
}

.question-header h2 {
  margin: 0 0 10px;
  color: #333;
}

.progress-info {
  margin-top: 15px;
}

.question-content {
  padding: 20px 0;
}

.question-text {
  font-size: 20px;
  color: #333;
  margin-bottom: 30px;
  line-height: 1.5;
}

.option-group {
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding: 12px 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  width: 100%;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.option-label {
  font-weight: 600;
  margin-right: 12px;
  color: #409eff;
}

.option-text {
  color: #606266;
}

.question-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>
