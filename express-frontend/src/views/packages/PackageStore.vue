<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { storePackage, updatePackage, getPackage } from "@/api/package";
import { COURIER_COMPANIES } from "@/utils/constants";

const router = useRouter();
const route = useRoute();
const loading = ref(false);
const isEditMode = ref(false);

const packageForm = ref({
  trackingNumber: "",
  courierCompany: "",
  recipientName: "",
  recipientPhone: "",
  roomNumber: "",
  shelfNumber: "",
  pickupCode: "",
  notes: "",
});

const generatePickupCode = () => {
  const timestamp = Date.now().toString().slice(-4);
  const random = Math.floor(Math.random() * 10000)
    .toString()
    .padStart(4, "0");
  return timestamp + random;
};

// ========== 手机号验证 ==========
const phoneError = ref("");
// 中国大陆手机号正则：1开头，第二位3-9，后面9位数字
const phoneRegex = /^1[3-9]\d{9}$/;

const validatePhone = () => {
  const phone = packageForm.value.recipientPhone;

  if (!phone) {
    phoneError.value = "请输入手机号";
    return false;
  }

  if (!phoneRegex.test(phone)) {
    phoneError.value = "请输入正确的11位手机号";
    return false;
  }

  phoneError.value = "";
  return true;
};

// 自动清除空格
const formatPhone = () => {
  packageForm.value.recipientPhone = packageForm.value.recipientPhone
    .replace(/\s/g, "")
    .trim();
  if (packageForm.value.recipientPhone) {
    validatePhone();
  } else {
    phoneError.value = "";
  }
};

const autoFill = async () => {
  if (packageForm.value.trackingNumber.length >= 5 && !isEditMode.value) {
    packageForm.value.pickupCode = generatePickupCode();
  }
};

const loadPackageData = async () => {
  const packageId = route.params.id;
  if (packageId) {
    isEditMode.value = true;
    loading.value = true;
    try {
      const res = await getPackage(packageId);
      Object.assign(packageForm.value, res);
    } catch (error) {
      ElMessage.error("加载快递信息失败");
    } finally {
      loading.value = false;
    }
  }
};

const handleSubmit = async () => {
  if (!packageForm.value.trackingNumber) {
    ElMessage.warning("请输入运单号");
    return;
  }

  // 验证手机号格式
  if (!validatePhone()) {
    ElMessage.warning(phoneError.value);
    return;
  }

  loading.value = true;
  try {
    if (isEditMode.value) {
      await updatePackage(route.params.id, packageForm.value);
      ElMessage.success("更新成功");
    } else {
      await storePackage(packageForm.value);
      ElMessage.success("入库成功");
    }
    router.push("/packages");
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.back();
};

onMounted(() => {
  loadPackageData();
});
</script>

<template>
  <div class="package-store">
    <h2 class="page-title">{{ isEditMode ? "编辑快递" : "快递入库" }}</h2>

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
            :disabled="loading || isEditMode"
            @blur="autoFill"
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

        <el-form-item label="手机号" required :error="phoneError">
          <el-input
            v-model="packageForm.recipientPhone"
            placeholder="请输入11位手机号"
            :disabled="loading"
            maxlength="11"
            show-word-limit
            @input="formatPhone"
            @blur="validatePhone"
          />
        </el-form-item>

        <el-form-item label="房号">
          <el-input
            v-model="packageForm.roomNumber"
            placeholder="请输入房号（如：1栋101）"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="货架号">
          <el-input
            v-model="packageForm.shelfNumber"
            placeholder="请输入货架编号"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="packageForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            :disabled="loading"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isEditMode ? "确认更新" : "确认入库" }}
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
