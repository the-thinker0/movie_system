<template>
  <div style="width: 50%">
    <div class="card" style="padding: 30px">
      <el-form :model="data.user" label-width="100px" style="padding-right: 50px">
        <div style="margin: 20px 0; text-align: center">
          <el-upload :show-file-list="false" class="avatar-uploader" :action="uploadUrl" :on-success="handleFileUpload">
            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
        <el-form-item label="账号">
          <el-input disabled v-model="data.user.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="data.user.name" autocomplete="off" />
        </el-form-item>
        <div v-if="data.user.role === 'USER'">
          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="data.user.sex">
              <el-radio label="男"></el-radio><el-radio label="女"></el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="data.user.phone" autocomplete="off" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="data.user.email" autocomplete="off" />
          </el-form-item>
        </div>
        <div style="text-align: center">
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {reactive} from "vue"
import request from "@/utils/request";
import {ElMessage} from "element-plus";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
})

const handleFileUpload = (file) => {
  data.user.avatar = file.data
}


const emit = defineEmits(["updateUser"]);

// 更新用户信息的函数
const updateUser = (url) => {
  request.put(url, data.user)
      .then(res => {
        if (res.code === '200') {
          ElMessage.success('更新成功');
          // 更新缓存中的用户信息
          localStorage.setItem('system-user', JSON.stringify(data.user));
          emit('updateUser');
        } else {
          ElMessage.error(res.msg);
        }
      })
      .catch(error => {
        ElMessage.error('网络请求失败，请稍后再试');
        console.error(error);  // 打印详细错误信息，便于调试
      });
};

// 保存用户信息
const save = () => {
  const url = data.user.role === 'ADMIN' ? '/admin/update' : '/user/update';
  updateUser(url);
};


</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}
</style>