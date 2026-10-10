<template>
  <div>
    <el-card>
      <div slot="header">
        <span>用户管理</span>
      </div>
      
      <el-table :data="userList" stripe>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="balance" label="余额" width="100">
          <template slot-scope="scope">
            ¥ {{ scope.row.balance }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button 
              v-if="scope.row.status === 1" 
              type="warning" 
              size="small" 
              @click="toggleStatus(scope.row.id, 0)"
            >禁用</el-button>
            <el-button 
              v-else 
              type="success" 
              size="small" 
              @click="toggleStatus(scope.row.id, 1)"
            >启用</el-button>
            <el-button type="primary" size="small" @click="resetPassword(scope.row.id)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'UserManage',
  data() {
    return {
      userList: []
    }
  },
  created() {
    this.fetchUsers()
  },
  methods: {
    async fetchUsers() {
      try {
        const res = await axios.get('/admin/users')
        if (res.data.code === 200) {
          this.userList = res.data.data
        }
      } catch (error) {
        this.$message.error('获取用户列表失败')
      }
    },
    async toggleStatus(id, status) {
      try {
        await axios.put(`/admin/users/${id}/status?status=${status}`)
        this.$message.success('更新成功')
        this.fetchUsers()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async resetPassword(id) {
      try {
        const res = await axios.post(`/admin/users/${id}/reset-password`)
        await this.$alert(
          `临时密码：${res.data.data.temporaryPassword}`,
          '密码已重置，请仅安全地交给该用户',
          { confirmButtonText: '我已记录', type: 'warning' }
        )
      } catch (error) {
        this.$message.error('重置失败')
      }
    }
  }
}
</script>
