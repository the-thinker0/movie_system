<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.orderNo" style="width: 300px; margin-right: 10px" placeholder="请输入订单编号查询"></el-input>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe>
        <el-table-column label="订单编号" prop="orderNo"></el-table-column>
        <el-table-column label="电影名称" prop="goodsName"></el-table-column>
        <el-table-column label="购买数量" prop="num"></el-table-column>
        <el-table-column label="下单人" prop="userName"></el-table-column>
        <el-table-column label="订单状态" prop="status">
          <template #default="scope">
            <el-tag v-if="scope.row.status==='已取消'" type="danger">已取消</el-tag>
            <el-tag v-if="scope.row.status==='待支付'" type="warning">待支付</el-tag>
            <el-tag v-if="scope.row.status==='待发货'" type="primary">待发货</el-tag>
            <el-tag v-if="scope.row.status==='待收货'" type="primary">待收货</el-tag>
            <el-tag v-if="scope.row.status==='已完成'" type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" prop="time"></el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button v-if="data.user.role==='USER' && scope.row.status==='待支付'" type="danger" @click="changeStatus(scope.row, '已取消')">取消</el-button>
            <el-button v-if="data.user.role==='USER' && scope.row.status==='待支付'" type="primary" @click="changeStatus(scope.row, '待发货')">支付</el-button>
            <el-button v-if="data.user.role==='ADMIN' && scope.row.status==='待发货'" type="primary" @click="changeStatus(scope.row, '待收货')">发货</el-button>
            <el-button v-if="data.user.role==='USER' && scope.row.status==='待收货'" type="primary" @click="changeStatus(scope.row, '已完成')">收货</el-button>
            <el-button v-if="data.user.role==='ADMIN'" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next"
                     :page-size="data.pageSize"
                     :current-page="data.pageNum"
                     :total="data.total"
                     @current-change="handlePageChange"
                     @size-change="handlePageSizeChange"/>
    </div>
  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive} from "vue";
import {ElMessageBox, ElMessage} from "element-plus";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  user :JSON.parse(localStorage.getItem('system-user') || '{}'),
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  orderNo: null
})

// 分页查询
const load = () => {
  request.get('/orders/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      orderNo: data.orderNo
    }
  }).then(res => {
    data.tableData = res.data?.list || [];
    data.total = res.data?.total || 0;
  })
}

// 编辑保存
const changeStatus = (row, status) => {
  row.status=status
  request.put('/orders/update', row).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

//查询
const search = () => {
  data.pageNum = 1;//查询时从第一页开始
  load();
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/orders/delete/' + id).then(res => {
      if (res.code === '200') {
        load()
        ElMessage.success('操作成功')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

// 重置
const reset = () => {
  data.orderNo = null
  data.pageNum = 1
  load()
}

// 处理文件上传的钩子
const handleImgSuccess = (res) => {
  data.form.avatar = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
}

// 分页页码变化
const handlePageChange = (page) => {
  data.pageNum = page; // 更新当前页码
  load();
};

// 每页条数变化
const handlePageSizeChange = (size) => {
  data.pageSize = size; // 更新每页条数
  data.pageNum = 1; // 重置页码为第一页
  load();
};

load()
</script>