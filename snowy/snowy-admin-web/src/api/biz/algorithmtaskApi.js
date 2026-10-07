import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/algorithmtask/` + url, ...arg)

/**
 * 算法题目任务Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取算法题目任务分页
	bizAlgorithmTaskPage(data) {
		return request('page', data, 'get')
	},
	// 提交算法题目任务表单 edit为true时为编辑，默认为新增
	bizAlgorithmTaskSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除算法题目任务
	bizAlgorithmTaskDelete(data) {
		return request('delete', data)
	},
	// 获取算法题目任务详情
	bizAlgorithmTaskDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载算法题目任务导入模板
    bizAlgorithmTaskDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入算法题目任务
	bizAlgorithmTaskImport(data) {
		return request('importData', data)
	},
	// 导出算法题目任务
	bizAlgorithmTaskExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
