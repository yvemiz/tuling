import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/forumpost/` + url, ...arg)

/**
 * 论坛帖子管理Api接口管理器
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
export default {
	// 获取论坛帖子管理分页
	bizForumPostPage(data) {
		return request('page', data, 'get')
	},
	// 提交论坛帖子管理表单 edit为true时为编辑，默认为新增
	bizForumPostSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除论坛帖子管理
	bizForumPostDelete(data) {
		return request('delete', data)
	},
	// 获取论坛帖子管理详情
	bizForumPostDetail(data) {
		return request('detail', data, 'get')
	},
	// 下载论坛帖子管理导入模板
    bizForumPostDownloadTemplate(data) {
		return request('downloadImportTemplate', data, 'get', {
			responseType: 'blob'
		})
    },
	// 导入论坛帖子管理
	bizForumPostImport(data) {
		return request('importData', data)
	},
	// 导出论坛帖子管理
	bizForumPostExport(data) {
		return request('exportData', data, 'post', {
			responseType: 'blob'
		})
	},
}
