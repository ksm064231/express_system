<script setup>
import { ref, onMounted } from "vue";
import { Search, Plus, Refresh } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getPackageList,
  deletePackage,
  searchPackages,
  getPackageByStatus,
} from "@/api/package";
import { PACKAGE_STATUS } from "@/utils/constants";

const loading = ref(false);
const packageList = ref([]);
const searchKeyword = ref("");
const statusFilter = ref("");

const statusOptions = Object.values(PACKAGE_STATUS).map((item) => ({
  label: item.label,
  value: item.value,
}));

const loadPackageList = async () => {
  loading.value = true;
  try {
    let res;
    if (searchKeyword.value) {
      res = await searchPackages(searchKeyword.value);
    } else if (statusFilter.value) {
      res = await getPackageByStatus(statusFilter.value);
    } else {
      res = await getPackageList();
    }
    packageList.value = res.data || [];
  } catch (error) {
    console.error("获取快递列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  loadPackageList();
};

const handleReset = () => {
  searchKeyword.value = "";
  statusFilter.value = "";
  loadPackageList();
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除这条快递记录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    await deletePackage(row.id);
    ElMessage.success("删除成功");
    loadPackageList();
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const getStatusTag = (status) => {
  const statusInfo = PACKAGE_STATUS[status] || { label: status, type: "info" };
  return {
    label: statusInfo.label,
    type: statusInfo.type,
  };
};

const formatPhone = (phone) => {
  if (!phone) return "";
  return phone.replace(/(\d{3})(\d{4})(\d{4})/, "$1****$3");
};

onMounted(() => {
  loadPackageList();
});
</script>

<template>
  <div class="package-list">
    <h2 class="page-title">快递管理</h2>

    <el-card>
      <div class="search-bar">
        <div class="search-form">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索运单号、收件人、电话、房号"
            style="width: 280px"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select
            v-model="statusFilter"
            placeholder="按状态筛选"
            style="width: 150px"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset"> 重置 </el-button>
        </div>
        <el-button type="primary" @click="$router.push('/packages/store')">
          <el-icon><Plus /></el-icon>
          快递入库
        </el-button>
      </div>

      <el-table
        :data="packageList"
        v-loading="loading"
        style="width: 100%; margin-top: 20px"
        border
      >
        <el-table-column prop="trackingNumber" label="运单号" width="160" />
        <el-table-column prop="courierCompany" label="快递公司" width="120" />
        <el-table-column prop="recipientName" label="收件人" width="100" />
        <el-table-column prop="recipientPhone" label="手机号" width="130">
          <template #default="{ row }">
            {{ formatPhone(row.recipientPhone) }}
          </template>
        </el-table-column>
        <el-table-column prop="roomNumber" label="房号" width="90" />
        <el-table-column prop="shelfNumber" label="货架号" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status).type">
              {{ getStatusTag(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pickupCode" label="取件码" width="100" />
        <el-table-column prop="arrivalTime" label="到达时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="$router.push(`/packages/edit/${row.id}`)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
