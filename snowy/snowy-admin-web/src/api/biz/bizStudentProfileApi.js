import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/studentprofile/` + url, ...arg)

/**
 * 学生档案管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取学生档案管理分页
	bizStudentProfilePage(data) {
		return request('page', data, 'get')
	},
	// 提交学生档案管理表单 edit为true时为编辑，默认为新增
	bizStudentProfileSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除学生档案管理
	bizStudentProfileDelete(data) {
		return request('delete', data)
	},
	// 获取学生档案管理详情
	bizStudentProfileDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载学生档案管理导入模板
    bizStudentProfileDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入学生档案管理
	bizStudentProfileImport(data) {
		return request('importData', data)
	},
	// 导出学生档案管理
	bizStudentProfileExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
