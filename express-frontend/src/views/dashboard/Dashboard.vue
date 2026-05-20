<script setup>
import { ref, onMounted } from "vue";
import {
  Box,
  TakeawayBox,
  RefreshLeft,
  Clock,
  Plus,
  Warning,
} from "@element-plus/icons-vue";
import { getDailyStatistics } from "@/api/statistics";

const loading = ref(false);
const statistics = ref({
  dailyStoredCount: 0,
  dailyPickupCount: 0,
  dailyReturnCount: 0,
  overdueCount: 0,
  currentStoredCount: 0,
  pendingExceptionCount: 0,
});

const loadStatistics = async () => {
  loading.value = true;
  try {
    const today = new Date().toISOString().split("T")[0];
    const res = await getDailyStatistics(today);
    statistics.value = res || statistics.value;
  } catch (error) {
    ElMessage.error("获取统计数据失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadStatistics();
});
</script>

<template>
  <div class="dashboard">
    <h2 class="page-title">数据统计</h2>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" :loading="loading">
          <div class="stat-content">
            <div class="stat-icon stored">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">
                {{ statistics.dailyStoredCount || 0 }}
              </div>
              <div class="stat-label">今日入库</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" :loading="loading">
          <div class="stat-content">
            <div class="stat-icon picked">
              <el-icon><TakeawayBox /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">
                {{ statistics.dailyPickupCount || 0 }}
              </div>
              <div class="stat-label">今日取件</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" :loading="loading">
          <div class="stat-content">
            <div class="stat-icon returned">
              <el-icon><RefreshLeft /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">
                {{ statistics.dailyReturnCount || 0 }}
              </div>
              <div class="stat-label">今日退件</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" :loading="loading">
          <div class="stat-content">
            <div class="stat-icon overdue">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.overdueCount || 0 }}</div>
              <div class="stat-label">逾期未取</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button
              type="primary"
              size="large"
              @click="$router.push('/packages/store')"
            >
              <el-icon><Plus /></el-icon>
              快递入库
            </el-button>
            <el-button
              type="success"
              size="large"
              @click="$router.push('/pickups')"
            >
              <el-icon><TakeawayBox /></el-icon>
              取件操作
            </el-button>
            <el-button
              type="warning"
              size="large"
              @click="$router.push('/exceptions')"
            >
              <el-icon><Warning /></el-icon>
              异常处理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.stored {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.picked {
  background: linear-gradient(135deg, #42b983 0%, #35a774 100%);
}

.stat-icon.returned {
  background: linear-gradient(135deg, #e6a23c 0%, #d6912b 100%);
}

.stat-icon.overdue {
  background: linear-gradient(135deg, #f56c6c 0%, #e45656 100%);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.quick-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  padding: 20px 0;
}
</style>
