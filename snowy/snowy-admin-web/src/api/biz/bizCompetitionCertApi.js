import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/competitioncert/` + url, ...arg)

/**
 * 竞赛证书管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取竞赛证书管理分页
	bizCompetitionCertPage(data) {
		return request('page', data, 'get')
	},
	// 提交竞赛证书管理表单 edit为true时为编辑，默认为新增
	bizCompetitionCertSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除竞赛证书管理
	bizCompetitionCertDelete(data) {
		return request('delete', data)
	},
	// 获取竞赛证书管理详情
	bizCompetitionCertDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载竞赛证书管理导入模板
    bizCompetitionCertDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入竞赛证书管理
	bizCompetitionCertImport(data) {
		return request('importData', data)
	},
	// 导出竞赛证书管理
	bizCompetitionCertExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
