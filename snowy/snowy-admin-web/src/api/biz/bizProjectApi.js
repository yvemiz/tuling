import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/project/` + url, ...arg)

/**
 * 项目团队管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取项目团队管理分页
	bizProjectPage(data) {
		return request('page', data, 'get')
	},
	// 提交项目团队管理表单 edit为true时为编辑，默认为新增
	bizProjectSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除项目团队管理
	bizProjectDelete(data) {
		return request('delete', data)
	},
	// 获取项目团队管理详情
	bizProjectDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载项目团队管理导入模板
    bizProjectDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入项目团队管理
	bizProjectImport(data) {
		return request('importData', data)
	},
	// 导出项目团队管理
	bizProjectExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
