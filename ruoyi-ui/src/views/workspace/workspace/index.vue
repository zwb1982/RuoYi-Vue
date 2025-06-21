<template>
  <div class="app-container">
    <!-- Query Form -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="工作空间名称" prop="workspaceName">
        <el-input
          v-model="queryParams.workspaceName"
          placeholder="请输入工作空间名称"
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- Action Buttons -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['workspace:workspace:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['workspace:workspace:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['workspace:workspace:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workspace:workspace:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- Data Table -->
    <el-table v-loading="loading" :data="workspaceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工作空间ID" align="center" prop="workspaceId" />
      <el-table-column label="工作空间名称" align="center" prop="workspaceName" />
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="创建者" align="center" prop="createBy" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
         <template slot-scope="scope">
          <!-- Assuming you have a dictionary for sys_normal_disable or similar -->
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['workspace:workspace:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workspace:workspace:remove']"
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

    <!-- Add/Edit Dialog (Simplified Placeholder) -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="工作空间名称" prop="workspaceName">
          <el-input v-model="form.workspaceName" placeholder="请输入工作空间名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
         <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// Import API functions for workspace
import { listWorkspace, getWorkspace, delWorkspace, addWorkspace, updateWorkspace } from "@/api/workspace/workspace";

export default {
  name: "WorkspaceManagement",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // Loading state
      loading: true,
      // IDs for selection
      ids: [],
      // Single selection disabled
      single: true,
      // Multiple selections disabled
      multiple: true,
      // Show search conditions
      showSearch: true,
      // Total records
      total: 0,
      // Workspace data
      workspaceList: [], // Will be populated by API
      // Dialog title
      title: "",
      // Show dialog
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        workspaceName: undefined,
        createBy: undefined,
        status: undefined // Ensure this matches the AiWorkspace domain if filtering by status
      },
      // Form data
      form: {}, // Reset in handleAdd/handleUpdate
      // Form validation rules
      rules: {
        workspaceName: [
          { required: true, message: "工作空间名称不能为空", trigger: "blur" }
        ],
        status: [ // Assuming status is part of the form and required
          { required: true, message: "状态不能为空", trigger: "change" } // Or "blur"
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** Query workspace list */
    getList() {
      this.loading = true;
      listWorkspace(this.queryParams).then(response => {
        this.workspaceList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
        // Optional: this.$modal.msgError("获取列表失败");
      });
    },
    // Cancel button
    cancel() {
      this.open = false;
      this.reset();
    },
    // Reset form
    reset() {
      this.form = {
        workspaceId: undefined,
        workspaceName: undefined,
        description: undefined,
        status: "0", // Default status for new items
        remark: undefined
      };
      this.resetForm("form");
    },
    /** Search button action */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** Reset button action */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // Handle multiple selections
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.workspaceId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** Add button action */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加AI工作空间";
    },
    /** Modify button action */
    handleUpdate(row) {
      this.reset();
      const workspaceId = row.workspaceId || this.ids[0];
      getWorkspace(workspaceId).then(response => {
        this.form = response.data; // Assuming response.data is the AiWorkspace object
        this.open = true;
        this.title = "修改AI工作空间";
      });
    },
    /** Submit button action */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // this.loading = true; // Consider a different loading var for dialog submit, e.g. this.submitLoading = true
          if (this.form.workspaceId != null) {
            updateWorkspace(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }); // .finally(() => { this.submitLoading = false; });
          } else {
            addWorkspace(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }); // .finally(() => { this.submitLoading = false; });
          }
        }
      });
    },
    /** Delete button action */
    handleDelete(row) {
      const workspaceIds = row.workspaceId || this.ids; // Can be array or single ID for backend
      this.$modal.confirm('是否确认删除AI工作空间编号为"' + workspaceIds + '"的数据项？').then(function() {
        return delWorkspace(workspaceIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** Export button action */
    handleExport() {
      this.download('workspace/workspace/export', { // Path matches AiWorkspaceController @PostMapping("/export")
        ...this.queryParams
      }, `workspace_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
