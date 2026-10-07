import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/memberscore/` + url, ...arg)

/**
 * 组员表现打分Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取组员表现打分分页
	bizMemberScorePage(data) {
		return request('page', data, 'get')
	},
	// 提交组员表现打分表单 edit为true时为编辑，默认为新增
	bizMemberScoreSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除组员表现打分
	bizMemberScoreDelete(data) {
		return request('delete', data)
	},
	// 获取组员表现打分详情
	bizMemberScoreDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载组员表现打分导入模板
    bizMemberScoreDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入组员表现打分
	bizMemberScoreImport(data) {
		return request('importData', data)
	},
	// 导出组员表现打分
	bizMemberScoreExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
