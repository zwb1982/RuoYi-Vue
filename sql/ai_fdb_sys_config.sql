-- AI-FDB System Configuration Settings
INSERT INTO sys_config (config_name, config_key, config_value, config_type, remark, create_by, create_time, update_by, update_time)
VALUES
('Qwen API密钥', 'ai.qwen.api.key', 'sk-beff2b8bc208457a9d971610488661f0', 'Y', 'Qwen AI API密钥', 'admin', sysdate(), 'admin', sysdate()),
('默认AI模型', 'ai.qwen.model.default', 'qwen-turbo', 'Y', '默认使用的AI模型', 'admin', sysdate(), 'admin', sysdate()),
('文件上传大小限制(字节)', 'ai.workspace.file.maxSize', '10485760', 'Y', '文件上传大小限制(字节), 10MB', 'admin', sysdate(), 'admin', sysdate()),
('AI工作空间功能开关', 'ai.workspace.enabled', 'true', 'Y', 'AI工作空间功能总开关', 'admin', sysdate(), 'admin', sysdate());
