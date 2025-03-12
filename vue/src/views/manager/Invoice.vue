<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.invoiceNo" style="width: 300px; margin-right: 10px" placeholder="请输入发票编号查询"></el-input>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe>
        <el-table-column label="发票编号" prop="invoiceNo"></el-table-column>
        <el-table-column label="订单编号" prop="orderNo"></el-table-column>
        <el-table-column label="电影名称" prop="goodsName"></el-table-column>
        <el-table-column label="购买数量" prop="num"></el-table-column>
        <el-table-column label="下单人" prop="userName"></el-table-column>
        <el-table-column label="下单时间" prop="time"></el-table-column>
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
  request.get('/invoice/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      invoiceNo: data.invoiceNo
    }
  }).then(res => {
    data.tableData = res.data?.list || [];
    data.total = res.data?.total || 0;
  })
}

// 编辑保存
const changeStatus = (row, status) => {
  row.status=status
  request.put('/invoice/update', row).then(res => {
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
    request.delete('/invoice/delete/' + id).then(res => {
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
  data.invoiceNo = null
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