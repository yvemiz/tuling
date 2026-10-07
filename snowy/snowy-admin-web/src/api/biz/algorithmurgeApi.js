import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/algorithmurge/` + url, ...arg)

/**
 * 算法催交记录Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取算法催交记录分页
	bizAlgorithmUrgePage(data) {
		return request('page', data, 'get')
	},
	// 提交算法催交记录表单 edit为true时为编辑，默认为新增
	bizAlgorithmUrgeSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除算法催交记录
	bizAlgorithmUrgeDelete(data) {
		return request('delete', data)
	},
	// 获取算法催交记录详情
	bizAlgorithmUrgeDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载算法催交记录导入模板
    bizAlgorithmUrgeDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入算法催交记录
	bizAlgorithmUrgeImport(data) {
		return request('importData', data)
	},
	// 导出算法催交记录
	bizAlgorithmUrgeExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
