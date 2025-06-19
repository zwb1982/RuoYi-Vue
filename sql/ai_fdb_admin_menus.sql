-- Parent Menu for AI Workspace Management
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('AI工作空间管理', 0, 10, 'ai-fdb', NULL, 1, 0, 'M', '0', '0', '', 'tool', 'admin', sysdate(), 'admin', sysdate(), 'AI工作空间管理主菜单');

-- Menu for Dashboard Overview (child of AI工作空间管理)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('仪表盘概览', (SELECT menu_id FROM sys_menu WHERE menu_name='AI工作空间管理' AND path='ai-fdb' LIMIT 1), 0, 'dashboard', 'workspace/dashboard/index', 1, 0, 'C', '0', '0', 'workspace:dashboard:view', 'dashboard', 'admin', sysdate(), 'admin', sysdate(), '仪表盘概览菜单');

-- Menu for Workspace Management (child of AI工作空间管理)
-- Let's assume the parent menu ID generated above is 2000 for this example.
-- In a real scenario, this ID would be auto-generated or carefully chosen.
-- For the component path, ruoyi-ui usually maps 'module/entity/index'.
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('工作空间管理', (SELECT menu_id FROM sys_menu WHERE menu_name='AI工作空间管理' AND path='ai-fdb' LIMIT 1), 1, 'workspace', 'workspace/workspace/index', 1, 0, 'C', '0', '0', 'workspace:workspace:list', 'component', 'admin', sysdate(), 'admin', sysdate(), '工作空间管理菜单');

-- Note: The SELECT for parent_id is a common way to get it if the script is run in one go.
-- If menu_id for 'AI工作空间管理' is known (e.g. 2000), it can be hardcoded for child items.
-- For subsequent menu items, they would also reference this parent_id.

-- Menu for Data Table Management (child of AI工作空间管理)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('数据表管理', (SELECT menu_id FROM sys_menu WHERE menu_name='AI工作空间管理' AND path='ai-fdb' LIMIT 1), 2, 'table', 'workspace/table/index', 1, 0, 'C', '0', '0', 'workspace:table:list', 'table', 'admin', sysdate(), 'admin', sysdate(), '数据表管理菜单');

-- Menu for Data Record Management (child of AI工作空间管理)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('数据记录管理', (SELECT menu_id FROM sys_menu WHERE menu_name='AI工作空间管理' AND path='ai-fdb' LIMIT 1), 3, 'record', 'workspace/record/index', 1, 0, 'C', '0', '0', 'workspace:record:list', 'documentation', 'admin', sysdate(), 'admin', sysdate(), '数据记录管理菜单');

-- Menu for File Management (child of AI工作空间管理)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('文件管理', (SELECT menu_id FROM sys_menu WHERE menu_name='AI工作空间管理' AND path='ai-fdb' LIMIT 1), 4, 'fileRelation', 'workspace/file/index', 1, 0, 'C', '0', '0', 'workspace:file:list', 'folder', 'admin', sysdate(), 'admin', sysdate(), '文件管理菜单');

-- Menu for AI Call Log (child of AI工作空间管理)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('AI调用日志', (SELECT menu_id FROM sys_menu WHERE menu_name='AI工作空间管理' AND path='ai-fdb' LIMIT 1), 5, 'aiLog', 'workspace/aiLog/index', 1, 0, 'C', '0', '0', 'workspace:ailog:list', 'bug', 'admin', sysdate(), 'admin', sysdate(), 'AI调用日志菜单');
