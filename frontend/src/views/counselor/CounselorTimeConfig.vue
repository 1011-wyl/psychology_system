<template>
  <div class="role-page counselor-page">
    <div class="role-page-heading">
      <div><span>日程管理</span><h1>可预约时间</h1></div>
      <p>按日期维护开放时段，让来访者看到准确安排</p>
    </div>
    <el-card>
      <div slot="header">
        <span>选择日期与时间段</span>
      </div>
      
      <el-form inline>
        <el-form-item label="选择日期">
          <el-date-picker v-model="selectedDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadTimeConfig">查询</el-button>
        </el-form-item>
      </el-form>

      <el-divider></el-divider>

      <div class="time-slots">
        <div class="slot-title">选择可预约时间段：</div>
        <el-checkbox-group v-model="selectedSlots">
          <el-checkbox v-for="slot in allTimeSlots" :key="slot" :label="slot" class="slot-item">
            {{ slot }}
          </el-checkbox>
        </el-checkbox-group>
      </div>

      <div class="save-btn">
        <el-button type="primary" @click="saveTimeConfig" :loading="saving">保存设置</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'CounselorTimeConfig',
  data() {
    return {
      selectedDate: '',
      allTimeSlots: ['09:00-10:00', '10:00-11:00', '11:00-12:00', '14:00-15:00', '15:00-16:00', '16:00-17:00'],
      selectedSlots: [],
      saving: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 86400000
        }
      }
    }
  },
  methods: {
    async loadTimeConfig() {
      if (!this.selectedDate) {
        this.$message.warning('请先选择日期')
        return
      }
      try {
        const res = await axios.get('/counselor/time-config', { params: { date: this.selectedDate } })
        if (res.data.code === 200) {
          this.selectedSlots = res.data.data.map(item => item.timeSlot)
        }
      } catch (error) {
        this.$message.error('加载失败')
      }
    },
    async saveTimeConfig() {
      if (!this.selectedDate) {
        this.$message.warning('请先选择日期')
        return
      }
      this.saving = true
      try {
        await axios.post('/counselor/time-config', {
          date: this.selectedDate,
          timeSlots: this.selectedSlots
        })
        this.$message.success('保存成功')
      } catch (error) {
        this.$message.error('保存失败')
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.time-slots {
  margin: 20px 0;
}
.slot-title {
  margin-bottom: 16px;
  font-weight: 600;
}
.slot-item {
  width: 150px;
  margin-bottom: 12px;
}
.save-btn {
  text-align: center;
  margin-top: 20px;
}
</style>
