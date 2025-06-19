<template>
  <div class="app-container">
    <!-- Query Form -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="记录ID" prop="recordId">
        <el-input v-model="queryParams.recordId" placeholder="请输入记录ID" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="字段ID" prop="fieldId">
        <el-input v-model="queryParams.fieldId" placeholder="请输入字段ID" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件名称" prop="fileName">
        <el-input v-model="queryParams.fileName" placeholder="请输入文件名称" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="文件类型" prop="fileType">
        <el-input v-model="queryParams.fileType" placeholder="例如：image/jpeg" clearable size="small" @keyup.enter.native="handleQuery"/>
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
          v-hasPermi="['workspace:file:remove']"
        >删除关联记录</el-button>
        <!-- Note: This deletes the relation, not necessarily the physical file -->
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workspace:file:export']"
        >导出列表</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- Data Table -->
    <el-table v-loading="loading" :data="fileRelationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="关联ID" align="center" prop="relationId" width="100"/>
      <el-table-column label="记录ID" align="center" prop="recordId" width="100"/>
      <el-table-column label="字段ID" align="center" prop="fieldId" width="100"/>
      <el-table-column label="文件名称" align="center" prop="fileName" :show-overflow-tooltip="true" />
      <el-table-column label="文件路径/链接" align="center" prop="filePath" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <!-- Assuming filePath might be a downloadable link or just a path string -->
          <a :href="makeDownloadableLink(scope.row.filePath)" target="_blank" v-if="isLink(scope.row.filePath)">{{ scope.row.filePath }}</a>
          <span v-else>{{ scope.row.filePath }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件大小" align="center" prop="fileSize" width="120">
        <template slot-scope="scope">
          <span>{{ formatFileSize(scope.row.fileSize) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="文件类型" align="center" prop="fileType" width="150" :show-overflow-tooltip="true"/>
      <el-table-column label="上传时间" align="center" prop="uploadTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
           <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['workspace:file:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workspace:file:remove']"
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

    <!-- View File Relation Details Dialog -->
    <el-dialog :title="title" :visible.sync="openView" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
         <el-row>
          <el-col :span="12"><el-form-item label="关联ID：">{{ form.relationId }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="记录ID：">{{ form.recordId }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="字段ID：">{{ form.fieldId }}</el-form-item></el-col>
          <el-col :span="24"><el-form-item label="文件名称：">{{ form.fileName }}</el-form-item></el-col>
          <el-col :span="24"><el-form-item label="文件路径：">{{ form.filePath }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="文件大小：">{{ formatFileSize(form.fileSize) }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="文件类型：">{{ form.fileType }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="上传者：">{{ form.createBy }}</el-form-item></el-col>
          <el-col :span="12"><el-form-item label="上传时间：">{{ parseTime(form.uploadTime) }}</el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注：">{{ form.remark }}</el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
// import { listFileRelation, getFileRelation, delFileRelation, exportFileRelation } from "@/api/workspace/file"; // To be created

export default {
  name: "FileManagement",
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      fileRelationList: [],
      title: "",
      openView: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        recordId: undefined,
        fieldId: undefined,
        fileName: undefined,
        fileType: undefined
      },
      form: {},
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      // Replace with: listFileRelation(this.queryParams).then(response => { ... });
      setTimeout(() => { // Mock API call
        this.fileRelationList = [
          { relationId: 1, recordId: 1001, fieldId: 50, fileName: "customer_avatar.jpg", filePath: "/profile/uploads/customer_avatar.jpg", fileSize: 102400, fileType: "image/jpeg", uploadTime: new Date(), createBy: "admin", remark: "Avatar" },
          { relationId: 2, recordId: 2001, fieldId: 65, fileName: "report_q1.pdf", filePath: "/profile/uploads/report_q1.pdf", fileSize: 2048000, fileType: "application/pdf", uploadTime: new Date(), createBy: "user1", remark: "" }
        ];
        this.total = this.fileRelationList.length;
        this.loading = false;
      }, 500);
    },
    formatFileSize(sizeInBytes) {
      if (sizeInBytes === null || sizeInBytes === undefined) return '';
      const units = ['B', 'KB', 'MB', 'GB', 'TB'];
      let i = 0;
      while(sizeInBytes >= 1024 && i < units.length -1 ) {
        sizeInBytes /= 1024;
        i++;
      }
      return sizeInBytes.toFixed(2) + ' ' + units[i];
    },
    isLink(filePath) {
        // Basic check if it's an absolute URL, could be more robust
        return filePath && (filePath.startsWith('http://') || filePath.startsWith('https://') || filePath.startsWith('/'));
    },
    makeDownloadableLink(filePath) {
        // If filePath is already a full URL, use it.
        // If it's a relative server path like '/profile/...', prepend base URL or handle as needed.
        // For this mock, assume it might be directly usable if it starts with '/'.
        if (filePath && filePath.startsWith('/profile')) { // Common RuoYi upload path
            // return process.env.VUE_APP_BASE_API + filePath; // If served through API gateway
            return filePath; // Or just the path if served directly by nginx/static server
        }
        return filePath;
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
      this.ids = selection.map(item => item.relationId);
      this.multiple = !selection.length;
    },
    handleView(row) {
      this.form = { ...row };
      this.openView = true;
      this.title = "文件关联详情";
    },
    handleDelete(row) {
      const relationIds = row.relationId || this.ids;
      this.$modal.confirm('是否确认删除文件关联记录编号为"' + relationIds + '"的数据项？这将只删除数据库记录，不会删除实际文件。').then(() => {
        // Replace with: return delFileRelation(relationIds);
        return new Promise(resolve => setTimeout(resolve, 500)); // Mock async
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除关联记录成功 (模拟)");
      }).catch(() => {});
    },
    handleExport() {
      this.download('workspace/file/export', { // Matches backend controller path
        ...this.queryParams
      }, `filerelation_${new Date().getTime()}.xlsx`);
       this.$modal.msgSuccess("导出列表操作（模拟）");
    }
  }
};
</script>
