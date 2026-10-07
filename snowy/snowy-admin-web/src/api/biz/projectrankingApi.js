import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/projectranking/` + url, ...arg)

/**
 * 项目综合排行榜Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取项目综合排行榜分页
	bizProjectRankingPage(data) {
		return request('page', data, 'get')
	},
	// 提交项目综合排行榜表单 edit为true时为编辑，默认为新增
	bizProjectRankingSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除项目综合排行榜
	bizProjectRankingDelete(data) {
		return request('delete', data)
	},
	// 获取项目综合排行榜详情
	bizProjectRankingDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载项目综合排行榜导入模板
    bizProjectRankingDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入项目综合排行榜
	bizProjectRankingImport(data) {
		return request('importData', data)
	},
	// 导出项目综合排行榜
	bizProjectRankingExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
