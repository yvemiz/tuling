import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/forumaudit/` + url, ...arg)

/**
 * 论坛审核记录Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取论坛审核记录分页
	bizForumAuditPage(data) {
		return request('page', data, 'get')
	},
	// 提交论坛审核记录表单 edit为true时为编辑，默认为新增
	bizForumAuditSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除论坛审核记录
	bizForumAuditDelete(data) {
		return request('delete', data)
	},
	// 获取论坛审核记录详情
	bizForumAuditDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载论坛审核记录导入模板
    bizForumAuditDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入论坛审核记录
	bizForumAuditImport(data) {
		return request('importData', data)
	},
	// 导出论坛审核记录
	bizForumAuditExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
