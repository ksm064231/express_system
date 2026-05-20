<script setup>
import { ref, onMounted } from "vue";
import { Search, Plus, Refresh } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getExceptionList,
  createException,
  resolveException,
  closeException,
} from "@/api/exception";
import { EXCEPTION_TYPE, EXCEPTION_STATUS } from "@/utils/constants";

const loading = ref(false);
const exceptionList = ref([]);
const searchKeyword = ref("");
const statusFilter = ref("");

const typeOptions = Object.values(EXCEPTION_TYPE).map((item) => ({
  label: item.label,
  value: item.value,
}));

const statusOptions = Object.values(EXCEPTION_STATUS).map((item) => ({
  label: item.label,
  value: item.value,
}));

const loadExceptionList = async () => {
  loading.value = true;
  try {
    const res = await getExceptionList();
    exceptionList.value = res || [];
  } catch (error) {
    ElMessage.error("获取异常列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  loadExceptionList();
};

const handleReset = () => {
  searchKeyword.value = "";
  statusFilter.value = "";
  loadExceptionList();
};

const handleResolve = async (row) => {
  try {
    await ElMessageBox.prompt("请输入处理结果", "处理异常", {
      confirmButtonText: "确认",
      cancelButtonText: "取消",
      inputPattern: /.+/,
      inputErrorMessage: "请输入处理结果",
    }).then(async ({ value }) => {
      await resolveException(row.id, { resolution: value });
      ElMessage.success("处理成功");
      loadExceptionList();
    });
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("处理失败");
    }
  }
};

const handleClose = async (row) => {
  try {
    await ElMessageBox.confirm(
      "确定要关闭这条异常记录吗？关闭后将无法再编辑。",
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );
    await closeException(row.id);
    ElMessage.success("关闭成功");
    loadExceptionList();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("关闭失败");
    }
  }
};

const getTypeLabel = (type) => {
  if (!type) return "-";
  const typeConfig = EXCEPTION_TYPE[type];
  return typeConfig ? typeConfig.label : type;
};

const getStatusLabel = (status) => {
  if (!status) return "-";
  const statusConfig = EXCEPTION_STATUS[status];
  return statusConfig ? statusConfig.label : status;
};

const getStatusType = (status) => {
  if (!status) return "info";
  const statusConfig = EXCEPTION_STATUS[status];
  return statusConfig ? statusConfig.type : "info";
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return "-";
  return new Date(dateTime).toLocaleString();
};

onMounted(() => {
  loadExceptionList();
});
</script>

<template>
  <div class="exception-list">
    <h2 class="page-title">异常处理</h2>

    <el-card>
      <div class="search-bar">
        <div class="search-form">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索运单号、异常描述"
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
        <el-button type="primary" @click="$router.push('/exceptions/create')">
          <el-icon><Plus /></el-icon>
          登记异常
        </el-button>
      </div>

      <el-table
        :data="exceptionList"
        v-loading="loading"
        style="width: 100%; margin-top: 20px"
        border
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="packageId" label="快递ID" width="90" />
        <el-table-column label="异常类型" width="130">
          <template #default="{ row }">
            <el-tag type="info">
              {{ getTypeLabel(row.exceptionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="异常描述" min-width="200" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理结果" min-width="180">
          <template #default="{ row }">
            {{ row.handlingResult || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="处理时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.handlingTime) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              type="success"
              size="small"
              link
              @click="handleResolve(row)"
            >
              处理
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              @click="$router.push(`/exceptions/edit/${row.id}`)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              @click="handleClose(row)"
            >
              关闭
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
