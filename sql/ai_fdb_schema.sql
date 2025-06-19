-- AI工作空间表
CREATE TABLE `ai_workspace` (
  `workspace_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工作空间ID',
  `workspace_name` varchar(100) NOT NULL COMMENT '工作空间名称',
  `description` text COMMENT '工作空间描述',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`workspace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI工作空间表';

-- AI数据表定义表
CREATE TABLE `ai_data_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据表ID',
  `workspace_id` bigint(20) NOT NULL COMMENT '所属工作空间ID',
  `table_name` varchar(100) NOT NULL COMMENT '数据表名称',
  `table_description` text COMMENT '数据表描述',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`),
  CONSTRAINT `fk_ai_data_table_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `ai_workspace` (`workspace_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI数据表定义表';

-- AI数据表字段定义表
CREATE TABLE `ai_table_field` (
  `field_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字段ID',
  `table_id` bigint(20) NOT NULL COMMENT '所属数据表ID',
  `field_name` varchar(100) NOT NULL COMMENT '字段名称',
  `field_label` varchar(255) NOT NULL COMMENT '字段显示标签',
  `field_type` varchar(50) NOT NULL COMMENT '字段类型',
  `field_description` text COMMENT '字段描述',
  `is_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填',
  `default_value` text COMMENT '默认值',
  `field_options` json COMMENT '字段选项配置',
  `extraction_prompt` text COMMENT 'AI抽取提示词',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`field_id`),
  CONSTRAINT `fk_ai_table_field_table` FOREIGN KEY (`table_id`) REFERENCES `ai_data_table` (`table_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI数据表字段定义表';

-- AI数据记录表
CREATE TABLE `ai_data_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `table_id` bigint(20) NOT NULL COMMENT '所属数据表ID',
  `record_data` json NOT NULL COMMENT '记录数据（JSON格式）',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`record_id`),
  CONSTRAINT `fk_ai_data_record_table` FOREIGN KEY (`table_id`) REFERENCES `ai_data_table` (`table_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI数据记录表';

-- AI记录文件关联表
CREATE TABLE `ai_record_file_relation` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `record_id` bigint(20) NOT NULL COMMENT '数据记录ID',
  `field_id` bigint(20) NOT NULL COMMENT '字段ID',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_name` varchar(255) NOT NULL COMMENT '文件名称',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`relation_id`),
  CONSTRAINT `fk_ai_record_file_record` FOREIGN KEY (`record_id`) REFERENCES `ai_data_record` (`record_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ai_record_file_field` FOREIGN KEY (`field_id`) REFERENCES `ai_table_field` (`field_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI记录文件关联表';
