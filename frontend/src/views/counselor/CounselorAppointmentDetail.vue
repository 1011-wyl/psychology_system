<template>
  <div>
    <el-card>
      <div slot="header">
        <span>预约详情</span>
        <el-button type="text" style="float: right" @click="$router.back()">返回</el-button>
      </div>

      <el-descriptions :column="2" border v-if="appointment">
        <el-descriptions-item label="预约日期">{{ appointment.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ appointment.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ appointment.userId }}</el-descriptions-item>
        <el-descriptions-item label="咨询费用">¥ {{ appointment.fee }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ appointment.problemDesc }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="statusType">{{ statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="诊断答复" :span="2" v-if="appointment.diagnosisReply">
          {{ appointment.diagnosisReply }}
        </el-descriptions-item>
        <el-descriptions-item label="答复时间" :span="2" v-if="appointment.replyTime">
          {{ formatDate(appointment.replyTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="action-buttons" v-if="appointment">
        <el-button 
          v-if="appointment.status === 0" 
          type="primary" 
          @click="confirmAppointment"
          :loading="confirmLoading"
        >确认预约</el-button>
        <el-button 
          v-if="appointment.status === 2" 
          type="success" 
          @click="openDiagnoseDialog"
        >填写诊断</el-button>
      </div>
    </el-card>

    <!-- 诊断弹窗 -->
    <el-dialog title="诊断答复" :visible.sync="diagnoseVisible" width="500px">
      <el-input type="textarea" v-model="diagnoseReply" :rows="5" placeholder="请输入诊断建议..."></el-input>
      <span slot="footer">
        <el-button @click="diagnoseVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDiagnose" :loading="diagnoseLoading">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'CounselorAppointmentDetail',
  data() {
    return {
      appointment: null,
      confirmLoading: false,
      diagnoseVisible: false,
      diagnoseReply: '',
      diagnoseLoading: false
    }
  },
  computed: {
    statusType() {
      if (!this.appointment) return 'info'
      const map = { 0: 'warning', 2: 'primary', 3: 'success', 4: 'success', 5: 'danger' }
      return map[this.appointment.status] || 'info'
    },
    statusText() {
      if (!this.appointment) return ''
      const map = { 0: '待确认', 2: '待诊断', 3: '已诊断', 4: '已评价', 5: '已取消' }
      return map[this.appointment.status] || '未知'
    }
  },
  created() {
    this.fetchDetail()
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    async fetchDetail() {
      const id = this.$route.params.id
      try {
        const res = await axios.get('/counselor/appointment/' + id)
        if (res.data.code === 200) {
          this.appointment = res.data.data
        }
      } catch (error) {
        this.$message.error('获取详情失败')
      }
    },
    async confirmAppointment() {
      this.confirmLoading = true
      try {
        await axios.post('/counselor/confirm', null, { params: { appointmentId: this.appointment.id } })
        this.$message.success('确认成功')
        this.fetchDetail()
      } catch (error) {
        this.$message.error('确认失败')
      } finally {
        this.confirmLoading = false
      }
    },
    openDiagnoseDialog() {
      this.diagnoseReply = ''
      this.diagnoseVisible = true
    },
    async submitDiagnose() {
      if (!this.diagnoseReply.trim()) {
        this.$message.warning('请输入诊断答复')
        return
      }
      this.diagnoseLoading = true
      try {
        await axios.post('/counselor/diagnose', {
          appointmentId: this.appointment.id,
          reply: this.diagnoseReply
        })
        this.$message.success('提交成功')
        this.diagnoseVisible = false
        this.fetchDetail()
      } catch (error) {
        this.$message.error('提交失败')
      } finally {
        this.diagnoseLoading = false
      }
    }
  }
}
</script>

<style scoped>
.action-buttons {
  margin-top: 20px;
  text-align: center;
}
</style>
