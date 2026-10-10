<template>
  <div>
    <el-card>
      <div slot="header">
        <span>预约管理</span>
      </div>
      
      <el-table :data="appointmentList" stripe>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="appointmentDate" label="日期" width="120"></el-table-column>
        <el-table-column prop="appointmentTime" label="时间" width="120"></el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
        <el-table-column prop="counselorId" label="咨询师ID" width="100"></el-table-column>
        <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.status === 0" 
              type="danger" 
              size="small" 
              @click="terminate(scope.row.id)"
            >终止预约</el-button>
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog title="预约详情" :visible.sync="detailVisible" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ currentAppointment.userId }}</el-descriptions-item>
        <el-descriptions-item label="咨询师ID">{{ currentAppointment.counselorId }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentAppointment.appointmentDate }} {{ currentAppointment.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ currentAppointment.problemDesc }}</el-descriptions-item>
        <el-descriptions-item label="诊断答复">{{ currentAppointment.diagnosisReply || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentAppointment.status) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'AppointmentManage',
  data() {
    return {
      appointmentList: [],
      detailVisible: false,
      currentAppointment: {}
    }
  },
  created() {
    this.fetchAppointments()
  },
  methods: {
    async fetchAppointments() {
      try {
        const res = await axios.get('/admin/appointments')
        if (res.data.code === 200) {
          this.appointmentList = res.data.data
        }
      } catch (error) {
        this.$message.error('获取预约列表失败')
      }
    },
    async terminate(id) {
      this.$confirm('确定终止该预约吗？', '提示', { type: 'warning' }).then(async () => {
        await axios.post(`/admin/appointments/${id}/terminate`)
        this.$message.success('已终止预约')
        this.fetchAppointments()
      }).catch(() => {})
    },
    viewDetail(row) {
      this.currentAppointment = row
      this.detailVisible = true
    },
    getStatusType(status) {
      const map = { 0: 'warning', 2: 'primary', 3: 'success', 4: 'success', 5: 'danger' }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = { 0: '待确认', 2: '待诊断', 3: '已诊断', 4: '已评价', 5: '已取消' }
      return map[status] || '未知'
    }
  }
}
</script>
