<template>
  <div class="app-container">
    <!-- Query Form -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="数据表ID" prop="tableId">
        <el-input
          v-model="queryParams.tableId"
          placeholder="请输入数据表ID"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建者" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入创建者"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="记录状态" clearable size="small">
          <el-option
            v-for="dict in dict.type.sys_normal_disable" <!-- Assuming same status codes -->
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
          v-hasPermi="['workspace:record:remove']"
        >删除</el-button>
      </el-col>
       <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workspace:record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- Data Table -->
    <el-table v-loading="loading" :data="dataRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="recordId" />
      <el-table-column label="数据表ID" align="center" prop="tableId" />
      <el-table-column label="记录数据 (部分)" align="center" prop="recordData" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ getRecordDataPreview(scope.row.recordData) }}</span>
        </template>
      </el-table-column>
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
            v-hasPermi="['workspace:record:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workspace:record:remove']"
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

    <!-- View Record Details Dialog -->
    <el-dialog :title="title" :visible.sync="openView" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
         <el-row>
          <el-col :span="12">
            <el-form-item label="记录ID：">{{ form.recordId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据表ID：">{{ form.tableId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建者：">{{ form.createBy }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间：">{{ parseTime(form.createTime) }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态：">
              <dict-tag :options="dict.type.sys_normal_disable" :value="form.status"/>
            </el-form-item>
          </el-col>
           <el-col :span="24">
            <el-form-item label="备注：">{{ form.remark }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="记录数据：">
              <pre>{{ formatJson(form.recordData) }}</pre>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import { listRecord, getRecord, delRecord, exportRecord } from "@/api/workspace/record"; // To be created

export default {
  name: "DataRecordManagement",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      dataRecordList: [],
      title: "",
      openView: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tableId: undefined,
        createBy: undefined,
        status: undefined
      },
      form: {}, // For viewing record details
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      // Replace with: listRecord(this.queryParams).then(response => { ... });
      setTimeout(() => { // Mock API call
        this.dataRecordList = [
          { recordId: 1001, tableId: 101, recordData: JSON.stringify({name: "张三", age: 30, email: "zhangsan@example.com"}), createBy: "admin", createTime: new Date(), status: "0", remark:"First record" },
          { recordId: 1002, tableId: 101, recordData: JSON.stringify({name: "李四", age: 25, email: "lisi@example.com"}), createBy: "user1", createTime: new Date(), status: "0", remark:"" },
          { recordId: 2001, tableId: 201, recordData: JSON.stringify({task: "完成报告", assignee: "王五", dueDate: "2024-08-15"}), createBy: "user1", createTime: new Date(), status: "0", remark:"" }
        ];
        this.total = this.dataRecordList.length;
        this.loading = false;
      }, 500);
    },
    getRecordDataPreview(jsonData) {
      try {
        const data = typeof jsonData === 'string' ? JSON.parse(jsonData) : jsonData;
        // Show first 2-3 keys as preview, or just a snippet of the string
        const keys = Object.keys(data);
        if (keys.length > 0) {
          return keys.slice(0,2).map(key => `${key}: ${data[key]}`).join(', ') + (keys.length > 2 ? '...' : '');
        }
        return JSON.stringify(data).substring(0, 50) + "...";
      } catch (e) {
        return String(jsonData).substring(0, 50) + "...";
      }
    },
    formatJson(jsonData) {
      try {
        const data = typeof jsonData === 'string' ? JSON.parse(jsonData) : jsonData;
        return JSON.stringify(data, null, 2);
      } catch (e) {
        return jsonData; // Return as is if not valid JSON
      }
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
      this.ids = selection.map(item => item.recordId);
      this.multiple = !selection.length;
    },
    handleView(row) {
      this.form = { ...row };
      this.openView = true;
      this.title = "数据记录详情";
    },
    handleDelete(row) {
      const recordIds = row.recordId || this.ids;
      this.$modal.confirm('是否确认删除数据记录编号为"' + recordIds + '"的数据项？').then(() => {
        // Replace with: return delRecord(recordIds);
        return new Promise(resolve => setTimeout(resolve, 500)); // Mock async
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功 (模拟)");
      }).catch(() => {});
    },
    handleExport() {
      // Exporting records with JSON data needs careful consideration for format.
      // This might involve a more complex export logic or server-side processing.
      this.download('workspace/record/export', {
        ...this.queryParams
      }, `datarecord_${new Date().getTime()}.xlsx`);
      this.$modal.msgSuccess("导出操作（模拟） - 调用下载方法. 实际导出JSON数据需特殊处理.");
    }
  }
};
</script>
<style scoped>
pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap; /* Handles long lines */
  word-break: break-all; /* Breaks long words/strings */
}
</style>
