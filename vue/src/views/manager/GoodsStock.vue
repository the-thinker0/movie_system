<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">新增</el-button>
      </div>
      <el-table :data="data.tableData" stripe>
        <el-table-column label="电影名称" prop="name"></el-table-column>
        <el-table-column label="上映时间" prop="showtime"></el-table-column>
        <!--        <el-table-column label="进货渠道" prop="channel"></el-table-column>-->
        <!--        <el-table-column label="进货日期" prop="date"></el-table-column>-->
        <el-table-column label="推荐理由" prop="comment"></el-table-column>
        <el-table-column label="上票数量" prop="tickets"></el-table-column>
        <el-table-column label="类型" prop="type"></el-table-column>
        <el-table-column label="票价" prop="price"></el-table-column>
        <el-table-column label="海报" prop="img">
          <template #default="scope">
            <el-image style= "width: 50px; height: 50px; border-radius: 5px" :src="scope.row.img"
                      :preview-src-list="[scope.row.img]" preview-teleported="true"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作" header-align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--    <div class="card" v-if="data.total">-->
    <!--      <el-pagination background layout="prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>-->
    <!--    </div>-->
    <div class="card" v-if="data.total">
      <el-pagination
          background
          layout="prev, pager, next, sizes, total"
          v-model:page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange">
      </el-pagination>
    </div>
    <el-dialog title="上映电影信息" width="40%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="电影名称" prop="name">
          <el-select v-model="data.form.goodsId" placeholder="请选择电影" style="width: 100%">
            <el-option
                v-for="item in data.goodsList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="海报" prop="img">
          <el-upload :action="uploadUrl" list-type="picture" :on-success="handleImgSuccess">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="进票数量">
          <el-input-number :min="1" v-model="data.form.tickets" ></el-input-number>
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="data.form.type"></el-input>
        </el-form-item>
        <el-form-item label="票价">
          <el-input-number :min="1" v-model="data.form.price" ></el-input-number>
        </el-form-item>
        <!--        <el-form-item label="电影主演">-->
        <!--          <el-input v-model="data.form.actors" placeholder="请输入电影主演" ></el-input>-->
        <!--        </el-form-item>-->
        <el-form-item label="上映日期">
          <div class="block">
            <el-date-picker
                v-model="data.form.showtime"
                type="datetime"
                placeholder="请选择上映日期"
                format="YYYY/MM/DD"
            />
          </div>
        </el-form-item>
        <el-form-item label="推荐理由">
          <el-input v-model="data.form.comment" placeholder="请输入推荐理由" ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">保 存</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive} from "vue";
import {ElMessageBox, ElMessage} from "element-plus";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  goodsName: null,
  goodsList: []
})

// 获取到农产品的数据
request.get('/goods/selectAll').then(res => {
  data.goodsList = res.data

})

// 分页查询
const load = () => {
  request.get('/goodsStock/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      goodsName: data.goodsName
    }
  }).then(res => {
    data.tableData = res.data?.list || [];
    data.total = res.data?.total || 0;
  })
}

// 新增
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 新增保存
const add = () => {
  request.post('/goodsStock/add', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 编辑保存
const update = () => {
  request.put('/goodsStock/update', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 弹窗保存
const save = () => {
  // 如果是新增或者更新，都要确保form里包含name字段
  const selectedGoods = data.goodsList.find(item => item.id === data.form.goodsId); // 根据goodsId找到对应的goods对象
  if (selectedGoods) {
    data.form.name = selectedGoods.name; // 将name字段添加到form对象中
  }
  // data.form有id就是更新，没有就是新增
  data.form.id ? update() : add()
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', {type: 'warning'}).then(res => {
    request.delete('/goodsStock/delete/' + id).then(res => {
      if (res.code === '200') {
        load()
        ElMessage.success('操作成功')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
  })
}

// 重置
const reset = () => {
  data.goodsName = null
  load()
}

// 处理文件上传的钩子
const handleImgSuccess = (res) => {
  data.form.img = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
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