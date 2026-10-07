import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/projectrecruit/` + url, ...arg)

/**
 * 项目招募需求Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取项目招募需求分页
	bizProjectRecruitPage(data) {
		return request('page', data, 'get')
	},
	// 提交项目招募需求表单 edit为true时为编辑，默认为新增
	bizProjectRecruitSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除项目招募需求
	bizProjectRecruitDelete(data) {
		return request('delete', data)
	},
	// 获取项目招募需求详情
	bizProjectRecruitDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载项目招募需求导入模板
    bizProjectRecruitDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入项目招募需求
	bizProjectRecruitImport(data) {
		return request('importData', data)
	},
	// 导出项目招募需求
	bizProjectRecruitExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
