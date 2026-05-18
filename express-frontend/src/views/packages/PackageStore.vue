<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { storePackage } from "@/api/package";
import { COURIER_COMPANIES } from "@/utils/constants";

const router = useRouter();
const loading = ref(false);

const packageForm = ref({
  trackingNumber: "",
  courierCompany: "",
  recipientName: "",
  recipientPhone: "",
  storageLocation: "",
  remarks: "",
});

const handleSubmit = async () => {
  if (!packageForm.value.trackingNumber || !packageForm.value.recipientPhone) {
    ElMessage.warning("请填写必填项");
    return;
  }

  loading.value = true;
  try {
    await storePackage(packageForm.value);
    ElMessage.success("入库成功");
    router.push("/packages");
  } catch (error) {
    console.error("入库失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.back();
};
</script>

<template>
  <div class="package-store">
    <h2 class="page-title">快递入库</h2>

    <el-card>
      <el-form
        :model="packageForm"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="运单号" required>
          <el-input
            v-model="packageForm.trackingNumber"
            placeholder="请输入快递运单号"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="快递公司">
          <el-select
            v-model="packageForm.courierCompany"
            placeholder="请选择快递公司"
            style="width: 100%"
            :disabled="loading"
          >
            <el-option
              v-for="company in COURIER_COMPANIES"
              :key="company"
              :label="company"
              :value="company"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="收件人" required>
          <el-input
            v-model="packageForm.recipientName"
            placeholder="请输入收件人姓名"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="手机号" required>
          <el-input
            v-model="packageForm.recipientPhone"
            placeholder="请输入收件人手机号"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="存放位置">
          <el-input
            v-model="packageForm.storageLocation"
            placeholder="请输入存放位置（如货架编号）"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="packageForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            确认入库
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
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
</style>
