import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/competition/` + url, ...arg)

/**
 * 竞赛信息管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取竞赛信息管理分页
	bizCompetitionPage(data) {
		return request('page', data, 'get')
	},
	// 提交竞赛信息管理表单 edit为true时为编辑，默认为新增
	bizCompetitionSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除竞赛信息管理
	bizCompetitionDelete(data) {
		return request('delete', data)
	},
	// 获取竞赛信息管理详情
	bizCompetitionDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载竞赛信息管理导入模板
    bizCompetitionDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入竞赛信息管理
	bizCompetitionImport(data) {
		return request('importData', data)
	},
	// 导出竞赛信息管理
	bizCompetitionExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
