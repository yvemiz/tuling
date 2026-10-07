import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/teacherprofile/` + url, ...arg)

/**
 * 教师档案管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取教师档案管理分页
	bizTeacherProfilePage(data) {
		return request('page', data, 'get')
	},
	// 提交教师档案管理表单 edit为true时为编辑，默认为新增
	bizTeacherProfileSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除教师档案管理
	bizTeacherProfileDelete(data) {
		return request('delete', data)
	},
	// 获取教师档案管理详情
	bizTeacherProfileDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载教师档案管理导入模板
    bizTeacherProfileDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入教师档案管理
	bizTeacherProfileImport(data) {
		return request('importData', data)
	},
	// 导出教师档案管理
	bizTeacherProfileExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
