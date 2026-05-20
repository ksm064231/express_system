<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { pickupByCode, getPickupRecords } from "@/api/pickup";
import { getPackageByTracking } from "@/api/package";
import { PACKAGE_STATUS } from "@/utils/constants";

const activeTab = ref("byCode");

// 凭取件码取件表单
const codeForm = reactive({
  trackingNumber: "",
  pickupCode: "",
  pickupPersonName: "",
});

// 管理员取件表单
const adminForm = reactive({
  trackingNumber: "",
  packageInfo: null,
  verificationMethod: "phone",
  pickupPersonName: "",
  pickupPersonPhone: "",
});

// 取件记录列表
const pickupRecords = ref([]);
const loading = ref(false);
const submitting = ref(false);

// 搜索快递
const searchPackage = async () => {
  if (!adminForm.trackingNumber.trim()) {
    ElMessage.warning("请输入运单号");
    return;
  }
  try {
    loading.value = true;
    const res = await getPackageByTracking(adminForm.trackingNumber);
    if (res && res.status === PACKAGE_STATUS.STORED.value) {
      adminForm.packageInfo = res;
      ElMessage.success("找到快递信息");
    } else if (res) {
      ElMessage.warning("该快递状态不是" + PACKAGE_STATUS.STORED.label);
    } else {
      ElMessage.warning("未找到该快递");
    }
  } catch (error) {
    ElMessage.error("查询失败");
  } finally {
    loading.value = false;
  }
};

// 凭取件码取件
const handlePickupByCode = async () => {
  if (!codeForm.trackingNumber || !codeForm.pickupCode) {
    ElMessage.warning("请填写运单号和取件码");
    return;
  }
  try {
    submitting.value = true;
    // 先查询快递获取ID（后端DTO验证需要packageId）
    const pkg = await getPackageByTracking(codeForm.trackingNumber);
    await pickupByCode(codeForm.trackingNumber, codeForm.pickupCode, {
      packageId: pkg.id,
      pickupPersonName: codeForm.pickupPersonName,
    });
    ElMessage.success("取件成功");
    // 重置表单
    codeForm.trackingNumber = "";
    codeForm.pickupCode = "";
    codeForm.pickupPersonName = "";
    // 刷新记录
    loadPickupRecords();
  } catch (error) {
    ElMessage.error(error.response?.data?.message || "取件失败");
  } finally {
    submitting.value = false;
  }
};

// 管理员取件
const handleAdminPickup = async () => {
  if (!adminForm.packageInfo) {
    ElMessage.warning("请先搜索并选择快递");
    return;
  }
  if (!adminForm.pickupPersonName || !adminForm.pickupPersonPhone) {
    ElMessage.warning("请填写取件人信息");
    return;
  }
  try {
    submitting.value = true;
    await pickupByCode(
      adminForm.trackingNumber,
      adminForm.packageInfo.pickupCode,
      {
        packageId: adminForm.packageInfo.id,
        pickupPersonName: adminForm.pickupPersonName,
        pickupPersonPhone: adminForm.pickupPersonPhone,
        verificationMethod: adminForm.verificationMethod,
      },
    );
    ElMessage.success("取件成功");
    // 重置表单
    adminForm.trackingNumber = "";
    adminForm.packageInfo = null;
    adminForm.pickupPersonName = "";
    adminForm.pickupPersonPhone = "";
    // 刷新记录
    loadPickupRecords();
  } catch (error) {
    ElMessage.error(error.response?.data?.message || "取件失败");
  } finally {
    submitting.value = false;
  }
};

// 加载取件记录
const loadPickupRecords = async () => {
  try {
    const res = await getPickupRecords();
    pickupRecords.value = res || [];
  } catch (error) {
    ElMessage.error("加载取件记录失败");
  }
};

// 状态标签类型
const getStatusType = (status) => {
  const statusConfig = PACKAGE_STATUS[status];
  return statusConfig?.type || "info";
};

const getStatusLabel = (status) => {
  const statusConfig = PACKAGE_STATUS[status];
  return statusConfig?.label || status;
};

// 组件挂载时加载数据
onMounted(() => {
  loadPickupRecords();
});
</script>

<template>
  <div class="pickup-operation">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>取件操作</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 凭取件码取件 -->
        <el-tab-pane label="凭取件码取件" name="byCode">
          <el-form :model="codeForm" label-width="100px" class="pickup-form">
            <el-form-item label="运单号">
              <el-input
                v-model="codeForm.trackingNumber"
                placeholder="请输入运单号"
                clearable
              />
            </el-form-item>
            <el-form-item label="取件码">
              <el-input
                v-model="codeForm.pickupCode"
                placeholder="请输入6位取件码"
                maxlength="6"
                clearable
              />
            </el-form-item>
            <el-form-item label="取件人姓名">
              <el-input
                v-model="codeForm.pickupPersonName"
                placeholder="请输入取件人姓名"
                clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handlePickupByCode"
                :loading="submitting"
              >
                确认取件
              </el-button>
              <el-button
                @click="
                  codeForm.trackingNumber = '';
                  codeForm.pickupCode = '';
                  codeForm.pickupPersonName = '';
                "
              >
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 管理员核验取件 -->
        <el-tab-pane label="管理员核验取件" name="admin">
          <el-form :model="adminForm" label-width="100px" class="pickup-form">
            <el-form-item label="运单号">
              <el-input
                v-model="adminForm.trackingNumber"
                placeholder="请输入运单号"
                clearable
                style="width: 300px; margin-right: 10px"
              />
              <el-button
                type="primary"
                :icon="Search"
                @click="searchPackage"
                :loading="loading"
              >
                搜索
              </el-button>
            </el-form-item>

            <!-- 快递信息展示 -->
            <div v-if="adminForm.packageInfo" class="package-info">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="运单号">
                  {{ adminForm.packageInfo.trackingNumber }}
                </el-descriptions-item>
                <el-descriptions-item label="收件人">
                  {{ adminForm.packageInfo.recipientName }}
                </el-descriptions-item>
                <el-descriptions-item label="收件电话">
                  {{ adminForm.packageInfo.recipientPhone }}
                </el-descriptions-item>
                <el-descriptions-item label="取件码">
                  <el-tag type="success">{{
                    adminForm.packageInfo.pickupCode
                  }}</el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <el-divider v-if="adminForm.packageInfo" />

            <template v-if="adminForm.packageInfo">
              <el-form-item label="验证方式">
                <el-radio-group v-model="adminForm.verificationMethod">
                  <el-radio label="phone">手机号验证</el-radio>
                  <el-radio label="idcard">身份证验证</el-radio>
                  <el-radio label="signature">签名确认</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="取件人姓名">
                <el-input
                  v-model="adminForm.pickupPersonName"
                  placeholder="请输入取件人姓名"
                  clearable
                />
              </el-form-item>
              <el-form-item label="取件人电话">
                <el-input
                  v-model="adminForm.pickupPersonPhone"
                  placeholder="请输入取件人电话"
                  clearable
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  @click="handleAdminPickup"
                  :loading="submitting"
                >
                  确认取件
                </el-button>
                <el-button
                  @click="
                    adminForm.trackingNumber = '';
                    adminForm.packageInfo = null;
                    adminForm.pickupPersonName = '';
                    adminForm.pickupPersonPhone = '';
                  "
                >
                  重置
                </el-button>
              </el-form-item>
            </template>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 取件记录 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>取件记录</span>
          <el-button type="primary" size="small" @click="loadPickupRecords">
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="pickupRecords" border stripe>
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="packageId" label="快递ID" width="80" />
        <el-table-column prop="pickupPersonName" label="取件人" width="120" />
        <el-table-column label="取件电话" width="140">
          <template #default="{ row }">
            {{ row.pickupPersonPhone || "-" }}
          </template>
        </el-table-column>
        <el-table-column
          prop="verificationMethod"
          label="验证方式"
          width="100"
        />
        <el-table-column prop="signature" label="取件码/签名" width="120" />
        <el-table-column label="取件时间" width="180">
          <template #default="{ row }">
            {{
              row.pickupTime ? new Date(row.pickupTime).toLocaleString() : "-"
            }}
          </template>
        </el-table-column>
        <el-table-column label="备注" width="150">
          <template #default="{ row }">
            {{ row.notes || "-" }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.pickup-operation {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pickup-form {
  max-width: 600px;
}

.package-info {
  margin-top: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>
