import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/competitionproject/` + url, ...arg)

/**
 * 竞赛报名关联Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取竞赛报名关联分页
	bizCompetitionProjectPage(data) {
		return request('page', data, 'get')
	},
	// 提交竞赛报名关联表单 edit为true时为编辑，默认为新增
	bizCompetitionProjectSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除竞赛报名关联
	bizCompetitionProjectDelete(data) {
		return request('delete', data)
	},
	// 获取竞赛报名关联详情
	bizCompetitionProjectDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载竞赛报名关联导入模板
    bizCompetitionProjectDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入竞赛报名关联
	bizCompetitionProjectImport(data) {
		return request('importData', data)
	},
	// 导出竞赛报名关联
	bizCompetitionProjectExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
