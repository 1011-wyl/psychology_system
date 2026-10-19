<template>
  <PortalShell role="user">
    <div class="form-container">
      <el-card class="form-card">
        <div class="form-header">
          <h2>填写预约信息</h2>
          <p>请完善以下信息，提交后等待咨询师确认</p>
        </div>

        <el-form
          :model="form"
          :rules="rules"
          ref="appointmentForm"
          label-width="100px"
        >
          <el-form-item label="咨询师">
            <span class="counselor-name">{{ counselorName }}</span>
            <span class="counselor-fee">（费用：¥ {{ fee }}）</span>
          </el-form-item>

          <el-form-item label="评估领域" prop="fieldId">
            <el-select v-model="form.fieldId" placeholder="请选择评估领域">
              <el-option
                v-for="item in fieldList"
                :key="item.id"
                :label="item.fieldName"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="预约日期" prop="date">
            <el-date-picker
              v-model="form.date"
              type="date"
              placeholder="选择日期"
              :picker-options="pickerOptions"
              value-format="yyyy-MM-dd"
              @change="onDateChange"
            >
            </el-date-picker>
          </el-form-item>

          <el-form-item label="预约时间" prop="timeSlot">
            <el-radio-group v-model="form.timeSlot">
              <el-radio-button
                v-for="slot in timeSlots"
                :key="slot"
                :label="slot"
                :disabled="!availableSlots.includes(slot)"
              >
              </el-radio-button>
            </el-radio-group>
            <div
              v-if="
                form.date && timeSlots.length > 0 && availableSlots.length === 0
              "
              class="no-slot-tip"
            >
              当天已无可用时间段，请选择其他日期
            </div>
          </el-form-item>

          <el-form-item label="问题描述" prop="problemDesc">
            <el-input
              type="textarea"
              v-model="form.problemDesc"
              :rows="5"
              placeholder="请描述您想要咨询的问题..."
            >
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button @click="goBack">返回</el-button>
            <el-button
              type="primary"
              @click="submitAppointment"
              :loading="submitting"
              >提交预约</el-button
            >
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </PortalShell>
</template>

<script>
import PortalShell from '@/components/PortalShell.vue'
import axios from '@/api/http'

export default {
  name: 'AppointmentForm',
  components: { PortalShell },
  data() {
    return {
      counselorId: null,
      counselorName: '',
      fee: 0,
      fieldList: [],
      timeSlots: [
        '09:00-10:00',
        '10:00-11:00',
        '11:00-12:00',
        '14:00-15:00',
        '15:00-16:00',
        '16:00-17:00',
      ],
      availableSlots: [],
      form: {
        fieldId: '',
        date: '',
        timeSlot: '',
        problemDesc: '',
      },
      submitting: false,
      pickerOptions: {
        disabledDate(time) {
          // 不能预约今天之前的日期
          return time.getTime() < Date.now() - 86400000
        },
      },
      rules: {
        fieldId: [
          { required: true, message: '请选择评估领域', trigger: 'change' },
        ],
        date: [
          { required: true, message: '请选择预约日期', trigger: 'change' },
        ],
        timeSlot: [
          { required: true, message: '请选择预约时间', trigger: 'change' },
        ],
        problemDesc: [
          { required: true, message: '请填写问题描述', trigger: 'blur' },
        ],
      },
    }
  },
  created() {
    this.counselorId = this.$route.query.counselorId
    this.counselorName = this.$route.query.counselorName
    this.fee = this.$route.query.fee
    this.fetchFields()
  },
  methods: {
    async fetchFields() {
      try {
        const res = await axios.get('/evaluate/fields')
        if (res.data.code === 200) {
          this.fieldList = res.data.data
        }
      } catch (error) {
        console.error('获取领域失败:', error)
      }
    },
    async onDateChange(date) {
      if (date) {
        try {
          const res = await axios.get('/appointment/time-slots', {
            params: { counselorId: this.counselorId, date: date },
          })
          if (res.data.code === 200) {
            this.availableSlots = res.data.data
            this.form.timeSlot = ''
          }
        } catch (error) {
          console.error('获取时间段失败:', error)
        }
      }
    },
    async submitAppointment() {
      this.$refs.appointmentForm.validate(async (valid) => {
        if (!valid) return

        this.submitting = true
        try {
          const res = await axios.post('/appointment/book', {
            counselorId: this.counselorId,
            fieldId: this.form.fieldId,
            date: this.form.date,
            timeSlot: this.form.timeSlot,
            problemDesc: this.form.problemDesc,
          })

          if (res.data.code === 200) {
            this.$message.success('预约成功！请等待咨询师确认')
            setTimeout(() => {
              this.$router.push('/appointment/list')
            }, 1500)
          } else {
            this.$message.error(res.data.msg || '预约失败')
          }
        } catch (error) {
          console.error('预约失败:', error)
          this.$message.error('提交失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    },
    goBack() {
      this.$router.back()
    },
  },
}
</script>

<style scoped>
.form-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.form-card {
  width: 600px;
  max-width: 90%;
  border-radius: 16px;
}

.form-header {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.form-header h2 {
  margin: 0 0 8px;
}

.form-header p {
  margin: 0;
  color: #909399;
}

.counselor-name {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.counselor-fee {
  color: #f56c6c;
  margin-left: 10px;
}

.no-slot-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 10px;
}
</style>
