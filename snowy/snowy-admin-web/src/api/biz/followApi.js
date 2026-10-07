import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/follow/` + url, ...arg)

/**
 * 学生关注管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取学生关注管理分页
	bizFollowPage(data) {
		return request('page', data, 'get')
	},
	// 提交学生关注管理表单 edit为true时为编辑，默认为新增
	bizFollowSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除学生关注管理
	bizFollowDelete(data) {
		return request('delete', data)
	},
	// 获取学生关注管理详情
	bizFollowDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载学生关注管理导入模板
    bizFollowDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入学生关注管理
	bizFollowImport(data) {
		return request('importData', data)
	},
	// 导出学生关注管理
	bizFollowExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
