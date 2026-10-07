import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/aievaluation/` + url, ...arg)

/**
 * 团队AI评估Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取团队AI评估分页
	bizAiEvaluationPage(data) {
		return request('page', data, 'get')
	},
	// 提交团队AI评估表单 edit为true时为编辑，默认为新增
	bizAiEvaluationSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除团队AI评估
	bizAiEvaluationDelete(data) {
		return request('delete', data)
	},
	// 获取团队AI评估详情
	bizAiEvaluationDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载团队AI评估导入模板
    bizAiEvaluationDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入团队AI评估
	bizAiEvaluationImport(data) {
		return request('importData', data)
	},
	// 导出团队AI评估
	bizAiEvaluationExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
