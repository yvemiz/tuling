import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/algorithmsuffix/` + url, ...arg)

/**
 * 算法作业提交Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取算法作业提交分页
	bizAlgorithmSubmissionPage(data) {
		return request('page', data, 'get')
	},
	// 提交算法作业提交表单 edit为true时为编辑，默认为新增
	bizAlgorithmSubmissionSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除算法作业提交
	bizAlgorithmSubmissionDelete(data) {
		return request('delete', data)
	},
	// 获取算法作业提交详情
	bizAlgorithmSubmissionDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载算法作业提交导入模板
    bizAlgorithmSubmissionDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入算法作业提交
	bizAlgorithmSubmissionImport(data) {
		return request('importData', data)
	},
	// 导出算法作业提交
	bizAlgorithmSubmissionExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
