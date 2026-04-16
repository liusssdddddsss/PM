import request from './request.js';

/**
 * 记录操作日志
 * @param {string} action - 操作内容
 * @param {string} module - 操作模块
 * @param {string|number} targetId - 操作目标ID
 * @param {string} targetName - 操作目标名称
 */
export const recordOperationLog = async (action, module, targetId = null, targetName = '') => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    console.log('localStorage user:', userStr);
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('User info:', user);
      // 调用后端API记录操作日志
      console.log('Sending operation log:', {
        username: user.username,
        action: targetName ? `${action}：${targetName}` : action,
        targetId: targetId,
        targetType: module,
        createTime: new Date().toISOString()
      });
      const response = await request.post('/workbench/operation-logs', {
        username: user.username,
        action: targetName ? `${action}：${targetName}` : action,
        targetId: targetId,
        targetType: module,
        createTime: new Date().toISOString()
      });
      console.log('Operation log response:', response);
    } else {
      console.error('No user info in localStorage');
    }
  } catch (error) {
    console.error('记录操作日志失败:', error);
  }
};
