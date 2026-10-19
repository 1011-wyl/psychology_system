<template>
  <div class="login-container role-login counselor-role-login">
    <section class="role-login-story">
      <router-link class="role-brand" to="/login">心理咨询与诊断平台</router-link>
      <div class="role-story-copy">
        <span>咨询师工作空间</span>
        <h1>把专注留给<br>每一次倾听</h1>
        <p>安排咨询时间、回应预约，在安静清晰的工作台里陪伴来访者。</p>
      </div>
      <div class="role-scene" aria-hidden="true">
        <i class="scene-sun"></i>
        <i class="scene-chair scene-chair--sage"></i>
        <i class="scene-table"></i>
        <i class="scene-chair scene-chair--coral"></i>
      </div>
    </section>
    <el-card class="login-card">
      <div class="login-header">
        <span class="login-kicker">欢迎回来</span>
        <h2>咨询师登录</h2>
        <p>进入你的咨询与预约工作台</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="loginForm">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="用户名"
            prefix-icon="el-icon-user"
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码"
            prefix-icon="el-icon-lock"
            size="large"
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-btn">
            {{ loading ? '登录中...' : '进入工作台' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <router-link to="/login">返回用户登录</router-link>
      </div>
      
      <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon @close="errorMsg = ''"></el-alert>
    </el-card>
  </div>
</template>

<script>
import axios, { getErrorMessage } from '@/api/http'

export default {
  name: 'CounselorLogin',
  data() {
    return {
      form: { username: '', password: '' },
      loading: false,
      errorMsg: '',
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        this.errorMsg = ''
        try {
          const res = await axios.post('/counselor/login', null, {
            params: { username: this.form.username, password: this.form.password }
          })
          if (res.data.code === 200) {
            localStorage.setItem('token', res.data.data.token)
            localStorage.setItem('counselor', JSON.stringify(res.data.data))
            this.$message.success('登录成功')
            this.$router.push('/counselor/home')
          } else {
            this.errorMsg = res.data.msg || '登录失败'
          }
        } catch (error) {
          console.error('登录错误:', error)
          this.errorMsg = getErrorMessage(error, '无法连接后端，请检查服务地址或跨域配置')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e2a3a 0%, #0f1419 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-card {
  width: 400px;
  border-radius: 12px;
}
.login-header {
  text-align: center;
  margin-bottom: 20px;
}
.login-header h2 {
  color: #1e2a3a;
  margin-bottom: 8px;
}
.login-header p {
  color: #909399;
}
.login-btn {
  width: 100%;
}
.login-footer {
  text-align: center;
  margin-top: 16px;
}
.login-footer a {
  color: #409eff;
  text-decoration: none;
}
</style>
