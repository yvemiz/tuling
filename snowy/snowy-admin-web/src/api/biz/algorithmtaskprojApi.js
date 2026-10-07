import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/algorithmtaskproj/` + url, ...arg)

/**
 * 算法任务指派Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取算法任务指派分页
	bizAlgorithmTaskProjPage(data) {
		return request('page', data, 'get')
	},
	// 提交算法任务指派表单 edit为true时为编辑，默认为新增
	bizAlgorithmTaskProjSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除算法任务指派
	bizAlgorithmTaskProjDelete(data) {
		return request('delete', data)
	},
	// 获取算法任务指派详情
	bizAlgorithmTaskProjDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载算法任务指派导入模板
    bizAlgorithmTaskProjDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入算法任务指派
	bizAlgorithmTaskProjImport(data) {
		return request('importData', data)
	},
	// 导出算法任务指派
	bizAlgorithmTaskProjExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
