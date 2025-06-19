<template>
  <div class="app-container">
    <!-- Query Form -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="工作空间ID" prop="workspaceId">
        <el-input
          v-model="queryParams.workspaceId"
          placeholder="请输入工作空间ID"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据表名称" prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入数据表名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="数据表状态" clearable size="small">
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- Action Buttons -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['workspace:table:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workspace:table:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- Data Table -->
    <el-table v-loading="loading" :data="dataTableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="数据表ID" align="center" prop="tableId" />
      <el-table-column label="工作空间ID" align="center" prop="workspaceId" />
      <el-table-column label="数据表名称" align="center" prop="tableName" :show-overflow-tooltip="true"/>
      <el-table-column label="描述" align="center" prop="tableDescription" :show-overflow-tooltip="true" />
      <el-table-column label="创建者" align="center" prop="createBy" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['workspace:table:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workspace:table:remove']"
          >删除</el-button>
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

    <!-- View Details Dialog (Placeholder) -->
    <el-dialog :title="title" :visible.sync="openView" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="数据表ID：">{{ form.tableId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作空间ID：">{{ form.workspaceId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据表名称：">{{ form.tableName }}</el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="状态：">
               <dict-tag :options="dict.type.sys_normal_disable" :value="form.status"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述：">{{ form.tableDescription }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建者：">{{ form.createBy }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间：">{{ parseTime(form.createTime) }}</el-form-item>
          </el-col>
           <el-col :span="24">
            <el-form-item label="备注：">{{ form.remark }}</el-form-item>
          </el-col>
        </el-row>
        <!-- Could also list fields here in a nested table -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// Import API functions for data table (to be created in @/api/workspace/table.js)
// import { listTable, getTable, delTable, exportTable } from "@/api/workspace/table";

export default {
  name: "DataTableManagement",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true, // Not used as no edit for single
      multiple: true,
      showSearch: true,
      total: 0,
      dataTableList: [],
      title: "",
      openView: false, // For details dialog
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        workspaceId: undefined,
        tableName: undefined,
        status: undefined
      },
      form: {}, // For storing details of the table being viewed
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      // Replace with: listTable(this.queryParams).then(response => { ... });
      setTimeout(() => { // Mock API call
        this.dataTableList = [
          { tableId: 101, workspaceId: 1, tableName: "客户表", tableDescription: "存储客户基本信息", createBy: "admin", createTime: new Date(), status: "0", remark: "Initial table" },
          { tableId: 102, workspaceId: 1, tableName: "订单表", tableDescription: "客户订单数据", createBy: "admin", createTime: new Date(), status: "0", remark: "" },
          { tableId: 201, workspaceId: 2, tableName: "任务列表", tableDescription: "项目Alpha的任务跟踪", createBy: "user1", createTime: new Date(), status: "0", remark: "" }
        ];
        this.total = this.dataTableList.length;
        this.loading = false;
      }, 500);
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
      this.$modal.msgSuccess("搜索操作（模拟）");
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tableId);
      this.multiple = !selection.length;
    },
    handleView(row) {
      this.form = { ...row }; // Use a copy
      this.openView = true;
      this.title = "数据表详情";
    },
    handleDelete(row) {
      const tableIds = row.tableId || this.ids;
      this.$modal.confirm('是否确认删除数据表编号为"' + tableIds + '"的数据项？').then(() => {
        // Replace with: return delTable(tableIds);
         return new Promise(resolve => setTimeout(resolve, 500)); // Mock async
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功 (模拟)");
      }).catch(() => {});
    },
    handleExport() {
      this.download('workspace/table/export', { // Matches backend controller
        ...this.queryParams
      }, `datatable_${new Date().getTime()}.xlsx`);
      this.$modal.msgSuccess("导出操作（模拟） - 调用下载方法");
    }
  }
};
</script>
