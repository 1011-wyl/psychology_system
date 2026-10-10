<template>
  <div class="login-container">
    <el-row type="flex" justify="center" align="middle" class="full-height">
      <el-col :xs="20" :sm="16" :md="10" :lg="8">
        <el-card class="login-card" shadow="hover">
          <div slot="header" class="login-header">
            <h2>心理咨询与诊断平台</h2>
            <p>欢迎登录</p>
          </div>
          
          <el-form :model="form" :rules="rules" ref="loginForm" label-width="0">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
                @keyup.enter="handleLogin"
              ></el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                @keyup.enter="handleLogin"
              ></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleLogin" 
                :loading="loading"
                class="login-btn"
              >
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
            
            <div class="register-link">
              还没有账号？
              <el-link type="primary" @click="$router.push('/register')">
                立即注册
              </el-link>
            </div>
          </el-form>
          
          <el-alert 
            v-if="errorMsg" 
            :title="errorMsg" 
            type="error" 
            show-icon
            :closable="true"
            @close="errorMsg = ''"
          ></el-alert>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios, { getErrorMessage } from '@/api/http'

export default {
  name: 'UserLogin',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false,
      errorMsg: ''
    }
  },
  methods: {
    async handleLogin() {
  this.$refs.loginForm.validate(async (valid) => {
    if (!valid) return
    
    this.loading = true
    this.errorMsg = ''
    
    try {
      // 修正这里：this.form.username 而不是 this.username
      const res = await axios.post('/user/login', null, {
        params: { username: this.form.username, password: this.form.password }
      })
      
      if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.data))
        this.$message.success('登录成功')
        this.$router.push('/home')
      } else {
        this.errorMsg = res.data.msg
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.full-height {
  min-height: 100vh;
  margin: 0;
}
.login-card {
  border-radius: 12px;
}
.login-header {
  text-align: center;
}
.login-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}
.login-header p {
  font-size: 14px;
  color: #909399;
}
.login-btn {
  width: 100%;
}
.register-link {
  text-align: center;
  margin-top: 16px;
}
</style>
