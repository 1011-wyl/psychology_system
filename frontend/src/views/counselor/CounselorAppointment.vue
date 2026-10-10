<template>
  <div>
    <el-card>
      <div slot="header">
        <span>预约列表</span>
      </div>
      
      <el-table :data="appointmentList" stripe>
        <el-table-column prop="appointmentDate" label="日期" width="120"></el-table-column>
        <el-table-column prop="appointmentTime" label="时间" width="120"></el-table-column>
        <el-table-column label="用户ID" prop="userId" width="100"></el-table-column>
        <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="fee" label="费用" width="100"></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.status === 0" 
              type="primary" 
              size="small" 
              @click="confirmAppointment(scope.row.id)"
            >确认</el-button>
            <el-button 
              v-if="scope.row.status === 2" 
              type="success" 
              size="small" 
              @click="openDiagnoseDialog(scope.row)"
            >诊断</el-button>
            <el-button type="text" size="small" @click="viewDetail(scope.row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
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
  name: 'CounselorAppointment',
  data() {
    return {
      appointmentList: [],
      diagnoseVisible: false,
      diagnoseReply: '',
      currentAppointmentId: null,
      diagnoseLoading: false
    }
  },
  created() {
    this.fetchAppointments()
  },
  methods: {
    async fetchAppointments() {
      try {
        const res = await axios.get('/counselor/appointments')
        if (res.data.code === 200) {
          this.appointmentList = res.data.data
        }
      } catch (error) {
        this.$message.error('获取预约列表失败')
      }
    },
    getStatusType(status) {
      const map = { 0: 'warning', 2: 'primary', 3: 'success', 4: 'success', 5: 'danger' }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = { 0: '待确认', 2: '待诊断', 3: '已诊断', 4: '已评价', 5: '已取消' }
      return map[status] || '未知'
    },
    async confirmAppointment(id) {
      try {
        await axios.post('/counselor/confirm', null, { params: { appointmentId: id } })
        this.$message.success('确认成功')
        this.fetchAppointments()
      } catch (error) {
        this.$message.error('确认失败')
      }
    },
    openDiagnoseDialog(row) {
      this.currentAppointmentId = row.id
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
          appointmentId: this.currentAppointmentId,
          reply: this.diagnoseReply
        })
        this.$message.success('提交成功')
        this.diagnoseVisible = false
        this.fetchAppointments()
      } catch (error) {
        this.$message.error('提交失败')
      } finally {
        this.diagnoseLoading = false
      }
    },
    viewDetail(id) {
      this.$router.push(`/counselor/appointment/detail/${id}`)
    }
  }
}
</script>
