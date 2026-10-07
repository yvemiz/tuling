import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/projectflow/` + url, ...arg)

/**
 * 组队申请流转Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取组队申请流转分页
	bizProjectFlowPage(data) {
		return request('page', data, 'get')
	},
	// 提交组队申请流转表单 edit为true时为编辑，默认为新增
	bizProjectFlowSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除组队申请流转
	bizProjectFlowDelete(data) {
		return request('delete', data)
	},
	// 获取组队申请流转详情
	bizProjectFlowDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载组队申请流转导入模板
    bizProjectFlowDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入组队申请流转
	bizProjectFlowImport(data) {
		return request('importData', data)
	},
	// 导出组队申请流转
	bizProjectFlowExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
