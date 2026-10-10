import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Alert from 'element-ui/lib/alert'
import Aside from 'element-ui/lib/aside'
import Avatar from 'element-ui/lib/avatar'
import Button from 'element-ui/lib/button'
import Card from 'element-ui/lib/card'
import Checkbox from 'element-ui/lib/checkbox'
import CheckboxGroup from 'element-ui/lib/checkbox-group'
import Col from 'element-ui/lib/col'
import Container from 'element-ui/lib/container'
import DatePicker from 'element-ui/lib/date-picker'
import Descriptions from 'element-ui/lib/descriptions'
import DescriptionsItem from 'element-ui/lib/descriptions-item'
import Dialog from 'element-ui/lib/dialog'
import Divider from 'element-ui/lib/divider'
import Dropdown from 'element-ui/lib/dropdown'
import DropdownItem from 'element-ui/lib/dropdown-item'
import DropdownMenu from 'element-ui/lib/dropdown-menu'
import Empty from 'element-ui/lib/empty'
import Form from 'element-ui/lib/form'
import FormItem from 'element-ui/lib/form-item'
import Header from 'element-ui/lib/header'
import Input from 'element-ui/lib/input'
import InputNumber from 'element-ui/lib/input-number'
import Link from 'element-ui/lib/link'
import Loading from 'element-ui/lib/loading'
import Main from 'element-ui/lib/main'
import Menu from 'element-ui/lib/menu'
import MenuItem from 'element-ui/lib/menu-item'
import Message from 'element-ui/lib/message'
import MessageBox from 'element-ui/lib/message-box'
import Option from 'element-ui/lib/option'
import Pagination from 'element-ui/lib/pagination'
import Progress from 'element-ui/lib/progress'
import Radio from 'element-ui/lib/radio'
import RadioButton from 'element-ui/lib/radio-button'
import RadioGroup from 'element-ui/lib/radio-group'
import Row from 'element-ui/lib/row'
import Select from 'element-ui/lib/select'
import Table from 'element-ui/lib/table'
import TableColumn from 'element-ui/lib/table-column'
import Tag from 'element-ui/lib/tag'
import 'element-ui/lib/theme-chalk/index.css'
import http, { configureHttp } from './api/http'

const elementComponents = [
  Alert, Aside, Avatar, Button, Card, Checkbox, CheckboxGroup, Col, Container,
  DatePicker, Descriptions, DescriptionsItem, Dialog, Divider, Dropdown,
  DropdownItem, DropdownMenu, Empty, Form, FormItem, Header, Input, InputNumber,
  Link, Main, Menu, MenuItem, Option, Pagination, Progress, Radio, RadioButton,
  RadioGroup, Row, Select, Table, TableColumn, Tag
]

elementComponents.forEach(component => Vue.use(component))
Vue.use(Loading.directive)
Vue.prototype.$loading = Loading.service
Vue.prototype.$message = Message
Vue.prototype.$msgbox = MessageBox
Vue.prototype.$alert = MessageBox.alert
Vue.prototype.$confirm = MessageBox.confirm
Vue.prototype.$prompt = MessageBox.prompt
Vue.config.productionTip = false
Vue.prototype.$http = http
configureHttp(router)

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
