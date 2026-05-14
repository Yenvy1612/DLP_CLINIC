/**
 * DLP Service — gọi các API DLP backend.
 *
 * Tất cả endpoint /api/dlp/* yêu cầu role ADMIN.
 * Service này được dùng bởi DlpDashboard.jsx
 */

import { httpGet, httpPost, toQueryString } from "./http";

// ==================== DANH SÁCH DLP LOGS ====================

/**
 * Lấy tất cả DLP violations, phân trang.
 * @param {number} page - Số trang (bắt đầu từ 0)
 * @param {number} size - Số record/trang
 */
export const getDlpLogs = (page = 0, size = 20) =>
    httpGet(`/api/dlp/logs${toQueryString({ page, size })}`);

/**
 * Lấy violations theo user cụ thể.
 * @param {number} userId
 * @param {number} page
 * @param {number} size
 */
export const getDlpLogsByUser = (userId, page = 0, size = 20) =>
    httpGet(`/api/dlp/logs/user/${userId}${toQueryString({ page, size })}`);

/**
 * Lấy violations theo mức rủi ro (LOW, MEDIUM, HIGH, CRITICAL).
 */
export const getDlpLogsByRiskLevel = (riskLevel, page = 0, size = 20) =>
    httpGet(`/api/dlp/logs/risk/${riskLevel}${toQueryString({ page, size })}`);

/**
 * Lấy violations theo loại (CCCD_DETECTED, SENSITIVE_WORD, ...).
 */
export const getDlpLogsByViolationType = (type, page = 0, size = 20) =>
    httpGet(`/api/dlp/logs/type/${type}${toQueryString({ page, size })}`);

// ==================== THỐNG KÊ ====================

/**
 * Lấy thống kê tổng hợp cho DLP Dashboard.
 * @param {string} from - Ngày bắt đầu (yyyy-MM-dd)
 * @param {string} to - Ngày kết thúc (yyyy-MM-dd)
 * @returns {Object} { totalViolations, totalBlocked, byRiskLevel, byViolationType }
 */
export const getDlpStats = (from, to) =>
    httpGet(`/api/dlp/logs/stats${toQueryString({ from, to })}`);

// ==================== QUẢN LÝ USER ====================

/** Khóa user do vi phạm DLP */
export const blockUser = (userId) =>
    httpPost(`/api/dlp/users/${userId}/block`);

/** Mở khóa user */
export const unblockUser = (userId) =>
    httpPost(`/api/dlp/users/${userId}/unblock`);
