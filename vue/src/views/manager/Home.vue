<template>
  <div style="display: flex;grid-gap: 10px" >
    <div style="width: 50%" class="card">
      <div style="padding-left: 20px; font-size: 20px; font-weight: bold; margin-bottom: 20px">推荐观看</div>
      <div v-for="(item,index) in data.filmList" :key= "item.id">
          <div style="display: flex;grid-gap: 10px;margin-bottom: 10px">
            <div style="font-weight: bold;font-size: 18px;color: gold" v-if="index === 0 ">1</div>
            <div style="font-weight: bold;font-size: 18px;color: silver" v-else-if="index === 1 ">2</div>
            <div style="font-weight: bold;font-size: 18px;color: lightcoral" v-else-if="index === 2 ">3</div>
            <div style="font-size: 18px;color: black" v-else>{{index+1}}</div>
            <img :src="item.img" alt="" style="width: 100px">
            <div style="flex: 1">
              <div style="font-size: 20px;margin-bottom: 10px">{{item.name}}</div>
              <div style="color: #666" class="line3">{{item.comment}}</div>
              <div style="color: black;margin-top: 10px" class="line3">{{item.type}}</div>
            </div>
          </div>
      </div>
    </div>




   <div>
    <div style="padding-left: 60px; font-size: 20px; font-weight: bold; margin-bottom: 20px">系统公告</div>
      <el-timeline style="with: 30%">
       <el-timeline-item :timestamp="item.time" placement="top" v-for="item in data.noticeList" :key="item.id">
<!--          <el-card>-->
         <div class="card" style="width: 700px">
           <h4>{{ item.title }}</h4>
           <p>{{ item.content }}</p>
         </div>
<!--          </el-card>-->
       </el-timeline-item>
     </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request";

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  noticeList: [],
  filmList:[]
})

//请求后台的公告数据
request.get('/notice/selectAll').then(res => {
  data.noticeList = res.data
})
request.get('/goodsStock/selectRanking').then(res => {
  data.filmList = res.data
})
</script>