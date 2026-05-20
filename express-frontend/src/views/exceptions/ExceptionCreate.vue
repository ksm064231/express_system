<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { createException } from "@/api/exception";
import { getPackageByTracking } from "@/api/package";
import { EXCEPTION_TYPE } from "@/utils/constants";

const router = useRouter();
const loading = ref(false);
const searching = ref(false);

const exceptionForm = ref({
  trackingNumber: "",
  packageId: null,
  exceptionType: "",
  description: "",
  packageInfo: null,
});

const typeOptions = Object.values(EXCEPTION_TYPE).map((item) => ({
  label: item.label,
  value: item.value,
}));

const searchPackage = async () => {
  if (!exceptionForm.value.trackingNumber.trim()) {
    ElMessage.warning("请输入运单号");
    return;
  }
  try {
    searching.value = true;
    const res = await getPackageByTracking(exceptionForm.value.trackingNumber);
    if (res) {
      exceptionForm.value.packageInfo = res;
      exceptionForm.value.packageId = res.id;
      ElMessage.success("找到快递信息");
    } else {
      ElMessage.warning("未找到该快递");
    }
  } catch (error) {
    ElMessage.error("查询失败");
  } finally {
    searching.value = false;
  }
};

const handleSubmit = async () => {
  if (!exceptionForm.value.packageId) {
    ElMessage.warning("请先搜索并选择快递");
    return;
  }
  if (!exceptionForm.value.exceptionType) {
    ElMessage.warning("请选择异常类型");
    return;
  }

  loading.value = true;
  try {
    await createException({
      packageId: exceptionForm.value.packageId,
      exceptionType: exceptionForm.value.exceptionType,
      description: exceptionForm.value.description,
    });
    ElMessage.success("登记成功");
    router.push("/exceptions");
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.back();
};
</script>

<template>
  <div class="exception-create">
    <h2 class="page-title">登记异常</h2>

    <el-card>
      <el-form
        :model="exceptionForm"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="运单号">
          <el-input
            v-model="exceptionForm.trackingNumber"
            placeholder="请输入运单号"
            style="width: 300px; margin-right: 10px"
            :disabled="loading"
            clearable
          />
          <el-button
            type="primary"
            :icon="Search"
            @click="searchPackage"
            :loading="searching"
          >
            搜索
          </el-button>
        </el-form-item>

        <!-- 快递信息展示 -->
        <div v-if="exceptionForm.packageInfo" class="package-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="运单号">
              {{ exceptionForm.packageInfo.trackingNumber }}
            </el-descriptions-item>
            <el-descriptions-item label="收件人">
              {{ exceptionForm.packageInfo.recipientName }}
            </el-descriptions-item>
            <el-descriptions-item label="收件电话">
              {{ exceptionForm.packageInfo.recipientPhone }}
            </el-descriptions-item>
            <el-descriptions-item label="快递公司">
              {{ exceptionForm.packageInfo.courierCompany }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider v-if="exceptionForm.packageInfo" />

        <template v-if="exceptionForm.packageInfo">
          <el-form-item label="异常类型" required>
            <el-select
              v-model="exceptionForm.exceptionType"
              placeholder="请选择异常类型"
              style="width: 100%"
              :disabled="loading"
            >
              <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="异常描述">
            <el-input
              v-model="exceptionForm.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述异常情况"
              :disabled="loading"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSubmit">
              确认登记
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </template>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.package-info {
  margin-top: 20px;
}
</style>
