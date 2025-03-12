<template>
  <div style="margin-bottom: 10px">
    <el-input size="large" style="width: 300px; margin-right: 10px;" v-model="data.name" placeholder="输入电影名称关键字查询"></el-input>
    <el-button type="primary" @click="search">查询</el-button>
    <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
  </div>
  <div>
    <el-row :gutter="10" v-if = "data.total > 0">
      <el-col style="margin-bottom: 10px" :span="6" v-for="item in data.goodsStockList" :key="item.id">
        <div class="card">
          <img :src="item.img" alt="" style="width: 100%; height: 400px">
          <div style="font-size: 18px; margin: 5px 0; color: #333333">{{item.name}}</div>
          <el-tooltip v-if="item.comment > 40" :content="item.comment" effect="light" placement="top">
            <div class="line2" style="margin: 5px 0; color: #666; font-size: 14px; height: 40px">{{ item.comment }}</div>
          </el-tooltip>
          <div v-else class="line2" style="margin: 5px 0; color: #666; font-size: 14px; height: 40px">{{ item.comment }}</div>
          <div style="margin: 5px 0; color: #666; font-size: 14px; height: 40px">上映时间：{{ item.showtime }}</div>
          <div style="margin: 10px 0"></div>
          <el-tag type="success">{{ item.type }}</el-tag>
          <div style="margin :10px 0; display: flex; align-items: center; color: #666">
            <div style="flex: 1">
              <strong style="font-size: 18px">¥{{ item.price }} </strong>
            </div>
            <div style="flex: 1; text-align: center">余票：{{ item.tickets }}</div>
            <div>
              <el-input-number @change="handleBuy(item)" v-model="item.num" style="width: 120px" :min="0" :max="999"></el-input-number>
            </div>
          </div>
          <div style="display: flex; align-items: center; justify-content: flex-end" v-if="item.num > 0">
            总价：<strong style="margin-right: 5px; display: inline-block; min-width: 50px; font-size: 18px; color: red">¥{{ item.total }}</strong>
            <el-button type="primary" @click="buy(item)">购买</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <div style="font-size: 30px; padding: 50px 0; text-align: center; color: #888888" v-else>暂无该电影...</div>
    <div style="margin-top: 10px" v-if="data.total">
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
import {reactive} from "vue";
import request from "@/utils/request";

const data=reactive({
  user :JSON.parse(localStorage.getItem('system-user') || '{}'),
  categoryList: [],
  pageNum: 1,
  pageSize: 4,
  total: 0,
  goodsStockList:[],
  name: '',
})

const buy = (goods) =>{
  let orderData = { goodsId: goods.id, num: goods.num, userId: data.user.id }
  request.post('/orders/add', orderData).then(res=>{
    if(res.code==='200'){
      ElMessage.success('购买成功')
      load()
    }else{
      ElMessage.error(res.msg)
    }
  })
}

const handleBuy = (goodsStock) => {
  goodsStock.total = (goodsStock.price * goodsStock.num).toFixed(2)    // 计算总价 保留2位小数
}

// /category/selectAll 是请求的 URL 路径，表示请求该接口
// then 方法是 Promise 对象的一个方法，用于处理异步操作成功后的回调
// res 是服务器返回的响应对象，通常包含响应的数据和其他元信息
request.get('/category/selectAll').then(res => {
  // 将服务器返回的数据赋值给 data.categoryList
  data.categoryList = res.data
})

// 分页查询
const load = () => {
  request.get('/goodsStock/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
    }
  }).then(res => {
    data.goodsStockList = res.data?.list || [];
    data.total = res.data?.total || 0;
    data.goodsStockList.forEach(item=>{
      item.num=0
    })
  })
}

// 分页查询辅助search函数
const search = () => {
  data.pageNum = 1;//查询时从第一页开始
  load();
}

// 重置
const reset = () => {
  data.name = null
  data.pageNum=1;   // 重置页码
  load()
}

// 处理页码变化
const handlePageChange = (pageNum) => {
  data.pageNum = pageNum;   // 更新当前页码
  load();  // 重新加载数据
};

// 处理每页显示条数变化
const handlePageSizeChange = (pageSize) => {
  data.pageSize = pageSize;   // 更新每页条数
  data.pageNum = 1;           // 重置页码为第一页
  load();  // 重新加载数据
};

load()
</script>
<style>
.highlight{
  color: white !important;
  background-color: #1976e3 !important;
}
</style>