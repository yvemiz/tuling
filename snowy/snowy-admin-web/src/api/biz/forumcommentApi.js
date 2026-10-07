import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/forumcomment/` + url, ...arg)

/**
 * 论坛评论管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取论坛评论管理分页
	bizForumCommentPage(data) {
		return request('page', data, 'get')
	},
	// 提交论坛评论管理表单 edit为true时为编辑，默认为新增
	bizForumCommentSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除论坛评论管理
	bizForumCommentDelete(data) {
		return request('delete', data)
	},
	// 获取论坛评论管理详情
	bizForumCommentDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载论坛评论管理导入模板
    bizForumCommentDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入论坛评论管理
	bizForumCommentImport(data) {
		return request('importData', data)
	},
	// 导出论坛评论管理
	bizForumCommentExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
