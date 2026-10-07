import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/projectmember/` + url, ...arg)

/**
 * 项目成员管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取项目成员管理分页
	bizProjectMemberPage(data) {
		return request('page', data, 'get')
	},
	// 提交项目成员管理表单 edit为true时为编辑，默认为新增
	bizProjectMemberSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除项目成员管理
	bizProjectMemberDelete(data) {
		return request('delete', data)
	},
	// 获取项目成员管理详情
	bizProjectMemberDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载项目成员管理导入模板
    bizProjectMemberDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入项目成员管理
	bizProjectMemberImport(data) {
		return request('importData', data)
	},
	// 导出项目成员管理
	bizProjectMemberExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
