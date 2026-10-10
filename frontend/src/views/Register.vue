<template>
  <div class="register-container">
    <el-row type="flex" justify="center" align="middle" class="full-height">
      <el-col :xs="22" :sm="18" :md="12" :lg="10">
        <el-card class="register-card" shadow="hover">
          <div slot="header" class="register-header">
            <h2>用户注册</h2>
            <p>填写信息，加入我们</p>
          </div>
          
          <el-form :model="form" :rules="rules" ref="registerForm" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
            </el-form-item>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
                    <el-option label="男" :value="1"></el-option>
                    <el-option label="女" :value="0"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input v-model="form.age" placeholder="请输入年龄"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            
            <el-form-item label="通讯地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入通讯地址"></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleRegister" :loading="loading" class="register-btn">
                {{ loading ? '注册中...' : '注册' }}
              </el-button>
            </el-form-item>
            
            <div class="login-link">
              已有账号？
              <el-link type="primary" @click="$router.push('/login')">
                立即登录
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
          <el-alert 
            v-if="successMsg" 
            :title="successMsg" 
            type="success" 
            show-icon
            :closable="true"
            @close="successMsg = ''"
          ></el-alert>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios, { getErrorMessage } from '@/api/http'

export default {
  name: 'UserRegister',
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        gender: 1,
        age: '',
        phone: '',
        address: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 8, max: 72, message: '长度在 8 到 72 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: (rule, value, callback) => {
              if (value !== this.form.password) {
                callback(new Error('两次输入密码不一致'))
              } else {
                callback()
              }
            }, trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入11位手机号', trigger: 'blur' }
        ]
      },
      loading: false,
      errorMsg: '',
      successMsg: ''
    }
  },
  methods: {
    async handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (!valid) return
        
        this.loading = true
        this.errorMsg = ''
        this.successMsg = ''
        
        try {
          const response = await axios.post('/user/register', {
            username: this.form.username,
            password: this.form.password,
            gender: this.form.gender,
            age: this.form.age ? Number(this.form.age) : null,
            phone: this.form.phone,
            address: this.form.address
          })
          
          if (response.data.code === 200) {
            this.successMsg = '注册成功！正在跳转到登录页...'
            setTimeout(() => {
              this.$router.push('/login')
            }, 1500)
          } else {
            this.errorMsg = response.data.msg || '注册失败'
          }
        } catch (error) {
          console.error('注册错误:', error)
          this.errorMsg = getErrorMessage(error, '网络错误，请稍后重试')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.full-height {
  min-height: 100vh;
  margin: 0;
}
.register-card {
  border-radius: 12px;
  margin: 30px 0;
}
.register-header {
  text-align: center;
}
.register-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}
.register-header p {
  font-size: 14px;
  color: #909399;
}
.register-btn {
  width: 100%;
}
.login-link {
  text-align: center;
  margin-top: 16px;
}
</style>
