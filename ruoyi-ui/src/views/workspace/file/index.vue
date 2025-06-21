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
// Import API functions for file relation
import { listFileRelation, getFileRelation, delFileRelation } from "@/api/workspace/file";

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
      listFileRelation(this.queryParams).then(response => {
        this.fileRelationList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    formatFileSize(sizeInBytes) {
      // ... (existing method, no changes needed) ...
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
      // ... (existing method, no changes needed) ...
      return filePath && (filePath.startsWith('http://') || filePath.startsWith('https://') || filePath.startsWith('/'));
    },
    makeDownloadableLink(filePath) {
      // ... (existing method, no changes needed) ...
      if (filePath && filePath.startsWith('/profile')) {
         // In a real RuoYi setup, '/dev-api' or VUE_APP_BASE_API might be needed if filePath is relative to backend root
         // For files served from /profile/upload/, they are often directly accessible if nginx is configured.
         // If using minio or other storage, this would be the direct URL.
         // Assuming filePath is a relative path that the browser can resolve or an absolute URL.
        return filePath;
      }
      return filePath;
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
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
      this.form = {}; // Clear
      const relationId = row.relationId || this.ids[0];
      getFileRelation(relationId).then(response => {
        this.form = response.data; // Assuming response.data is the AiRecordFileRelation object
        this.openView = true;
        this.title = "文件关联详情";
      });
    },
    handleDelete(row) {
      const relationIds = row.relationId || this.ids;
      this.$modal.confirm('是否确认删除文件关联记录编号为"' + relationIds + '"的数据项？注意：这只删除数据库记录，不会删除服务器上的实际文件。').then(function() {
        return delFileRelation(relationIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除关联记录成功");
      }).catch(() => {});
    },
    handleExport() {
      // Assuming an export endpoint /workspace/file/export exists for metadata export
      // If not, this would need to be added to AiRecordFileController
      // Or, implement client-side export of the current table data.
      this.download('workspace/file/export', {
        ...this.queryParams
      }, `filerelation_metadata_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
