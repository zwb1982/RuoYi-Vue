<template>
  <div class="app-container dashboard-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6" class="card-col">
        <div class="stat-card">
          <div class="stat-icon icon-workspace">
            <i class="el-icon-folder"></i>
          </div>
          <div class="stat-details">
            <div class="stat-label">工作空间总数</div>
            <div class="stat-value">{{ stats.workspaceCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" class="card-col">
        <div class="stat-card">
          <div class="stat-icon icon-table">
            <i class="el-icon-tickets"></i>
          </div>
          <div class="stat-details">
            <div class="stat-label">数据表总数</div>
            <div class="stat-value">{{ stats.tableCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" class="card-col">
        <div class="stat-card">
          <div class="stat-icon icon-record">
            <i class="el-icon-document-copy"></i>
          </div>
          <div class="stat-details">
            <div class="stat-label">数据记录总数</div>
            <div class="stat-value">{{ stats.recordCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" class="card-col">
        <div class="stat-card">
          <div class="stat-icon icon-ai">
            <i class="el-icon-cpu"></i>
          </div>
          <div class="stat-details">
            <div class="stat-label">今日AI调用 (模拟)</div>
            <div class="stat-value">{{ stats.aiCallCountToday }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Placeholder for charts or more detailed info -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card header="近期活动概览 (占位)">
          <p>这里可以放置图表或近期活动列表...</p>
          <el-empty description="图表数据加载中..."></el-empty>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// import { getDashboardStats } from "@/api/workspace/dashboard"; // To be created

export default {
  name: "WorkspaceDashboard",
  data() {
    return {
      loading: true,
      stats: {
        workspaceCount: 0,
        tableCount: 0,
        recordCount: 0,
        aiCallCountToday: 0
      }
    };
  },
  created() {
    this.fetchStats();
  },
  methods: {
    fetchStats() {
      this.loading = true;
      // Replace with: getDashboardStats().then(response => { this.stats = response.data; ... });
      setTimeout(() => { // Mock API call
        this.stats = {
          workspaceCount: 15,
          tableCount: 128,
          recordCount: 15760,
          aiCallCountToday: 230
        };
        this.loading = false;
      }, 500);
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
}

.card-col {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  }
}

.stat-icon {
  font-size: 40px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  margin-right: 20px;
  color: #fff;

  &.icon-workspace { background-color: #409EFF; }
  &.icon-table { background-color: #67C23A; }
  &.icon-record { background-color: #E6A23C; }
  &.icon-ai { background-color: #909399; } // Or a more distinct color
}

.stat-details {
  .stat-label {
    font-size: 14px;
    color: #888;
    margin-bottom: 5px;
  }
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
  }
}
.el-card {
    min-height: 200px; /* Ensure cards have some height */
}
</style>
