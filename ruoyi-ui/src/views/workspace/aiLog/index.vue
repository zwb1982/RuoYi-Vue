<template>
  <div class="app-container">
    <!-- Query Form -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="调用服务" prop="serviceName">
        <el-input v-model="queryParams.serviceName" placeholder="例如：generateDescription" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="调用者" prop="username">
        <el-input v-model="queryParams.username" placeholder="请输入调用者用户名" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="success">
        <el-select v-model="queryParams.success" placeholder="调用状态" clearable size="small">
          <el-option label="成功" :value="true" />
          <el-option label="失败" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item label="调用日期">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
       <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workspace:ailog:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- Data Table -->
    <el-table v-loading="loading" :data="aiLogList">
      <el-table-column label="日志ID" align="center" prop="logId" width="100"/>
      <el-table-column label="调用服务" align="center" prop="serviceName" :show-overflow-tooltip="true"/>
      <el-table-column label="调用者" align="center" prop="username" width="120"/>
      <el-table-column label="调用时间" align="center" prop="callTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.callTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="耗时(ms)" align="center" prop="duration" width="100"/>
      <el-table-column label="状态" align="center" prop="success" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.success ? 'success' : 'danger'">{{ scope.row.success ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
       <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['workspace:ailog:query']"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- View AI Log Details Dialog -->
    <el-dialog :title="title" :visible.sync="openView" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="12"><el-form-item label="日志ID：">{{ form.logId }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="调用者：">{{ form.username }}</el-form-item></el-col>
          <el-col :span="24"><el-form-item label="调用服务：">{{ form.serviceName }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="调用时间：">{{ parseTime(form.callTime) }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="耗时(ms)：">{{ form.duration }}</el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="状态：">
              <el-tag :type="form.success ? 'success' : 'danger'">{{ form.success ? '成功' : '失败' }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="请求参数："><pre>{{ formatJson(form.requestPayload) }}</pre></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="响应结果："><pre>{{ formatJson(form.responsePayload) }}</pre></el-form-item></el-col>
          <el-col :span="24" v-if="!form.success"><el-form-item label="错误信息："><pre>{{ form.errorMessage }}</pre></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import { listAiLog, getAiLog, exportAiLog } from "@/api/workspace/aiLog"; // To be created

export default {
  name: "AiCallLog",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      aiLogList: [],
      title: "",
      openView: false,
      dateRange: [], // For date range picker
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serviceName: undefined,
        username: undefined,
        success: undefined,
        // params will also include date range if set
      },
      form: {}, // For viewing log details
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      // Add date range to query params
      const query = this.addDateRange(this.queryParams, this.dateRange);
      // Replace with: listAiLog(query).then(response => { ... });
      setTimeout(() => { // Mock API call
        this.aiLogList = [
          { logId: 1, serviceName: "generateTableDescription", username: "admin", callTime: new Date(Date.now() - 3600000), duration: 1200, success: true, requestPayload: {tableName: "客户表"}, responsePayload: {description: "AI生成的描述..."} },
          { logId: 2, serviceName: "generateFieldSuggestions", username: "user1", callTime: new Date(Date.now() - 7200000), duration: 2500, success: true, requestPayload: {tableName: "订单表", tableDescription: "客户订单"}, responsePayload: [{fieldName: "orderId"}, {fieldName: "amount"}] },
          { logId: 3, serviceName: "extractDataFromText", username: "admin", callTime: new Date(), duration: 800, success: false, requestPayload: {text: "...", tableId: 101}, responsePayload: null, errorMessage: "API rate limit exceeded" }
        ];
        this.total = this.aiLogList.length;
        this.loading = false;
      }, 500);
    },
    formatJson(jsonData) {
      if (jsonData === null || jsonData === undefined) return 'N/A';
      try {
        const data = typeof jsonData === 'string' ? JSON.parse(jsonData) : jsonData;
        return JSON.stringify(data, null, 2);
      } catch (e) {
        return String(jsonData);
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
      this.$modal.msgSuccess("搜索操作（模拟）");
    },
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleView(row) {
      this.form = { ...row };
      this.openView = true;
      this.title = "AI调用日志详情";
    },
    handleExport() {
      const query = this.addDateRange(this.queryParams, this.dateRange);
      this.download('workspace/aiLog/export', { // Assuming backend controller path
        ...query
      }, `ailog_${new Date().getTime()}.xlsx`);
       this.$modal.msgSuccess("导出操作（模拟）");
    }
  }
};
</script>
<style scoped>
pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
