<template>
  <div>
    <el-card>
      <div slot="header">
        <span>咨询师管理</span>
        <el-button type="primary" size="small" style="float: right" @click="openAddDialog">添加咨询师</el-button>
      </div>
      
      <el-table :data="counselorList" stripe>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="title" label="职称" width="150"></el-table-column>
        <el-table-column prop="fee" label="费用" width="100">
          <template slot-scope="scope">¥ {{ scope.row.fee }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteCounselor(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名" v-if="!isEdit">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="费用">
          <el-input v-model="form.fee" type="number"></el-input>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCounselor">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from '@/api/http'

export default {
  name: 'CounselorManage',
  data() {
    return {
      counselorList: [],
      dialogVisible: false,
      isEdit: false,
      form: {
        id: null,
        username: '',
        realName: '',
        phone: '',
        title: '',
        fee: 0,
        description: ''
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑咨询师' : '添加咨询师'
    }
  },
  created() {
    this.fetchCounselors()
  },
  methods: {
    async fetchCounselors() {
      try {
        const res = await axios.get('/admin/counselors')
        if (res.data.code === 200) {
          this.counselorList = res.data.data
        }
      } catch (error) {
        this.$message.error('获取咨询师列表失败')
      }
    },
    openAddDialog() {
      this.isEdit = false
      this.form = { username: '', realName: '', phone: '', title: '', fee: 0, description: '' }
      this.dialogVisible = true
    },
    openEditDialog(row) {
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async saveCounselor() {
      try {
        if (this.isEdit) {
          await axios.put(`/admin/counselors/${this.form.id}`, this.form)
          this.$message.success('更新成功')
        } else {
          const res = await axios.post('/admin/counselors', this.form)
          await this.$alert(
            `临时密码：${res.data.data.temporaryPassword}`,
            '咨询师已添加，请仅安全地交付临时密码',
            { confirmButtonText: '我已记录', type: 'warning' }
          )
        }
        this.dialogVisible = false
        this.fetchCounselors()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async deleteCounselor(id) {
      this.$confirm('确定删除该咨询师吗？', '提示', { type: 'warning' }).then(async () => {
        await axios.delete(`/admin/counselors/${id}`)
        this.$message.success('删除成功')
        this.fetchCounselors()
      }).catch(() => {})
    }
  }
}
</script>
