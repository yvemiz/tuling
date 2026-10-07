import { baseRequest } from '@/utils/request'

export default {
	// 获取前台全部配置
	portalConfigGetAll() {
		return baseRequest('/portal/config/getAll', {}, 'get')
	},
	// 保存前台配置
	portalConfigSave(data) {
		return baseRequest('/portal/config/save', data, 'post')
	},
	// 分页查询社区论坛板块
	portalBoardPage(data) {
		return baseRequest('/portal/board/page', data, 'get')
	},
	// 板块列表
	portalBoardList() {
		return baseRequest('/portal/board/list', {}, 'get')
	},
	// 新增板块
	portalBoardAdd(data) {
		return baseRequest('/portal/board/add', data, 'post')
	},
	// 编辑板块
	portalBoardEdit(data) {
		return baseRequest('/portal/board/edit', data, 'post')
	},
	// 删除板块
	portalBoardDelete(data) {
		return baseRequest('/portal/board/delete', data, 'post')
	},
	// 变更板块状态
	portalBoardChangeStatus(data) {
		return baseRequest('/portal/board/changeStatus', data, 'post')
	},
	// 分页查询前台项目与推荐状态
	portalProjectPage(data) {
		return baseRequest('/portal/project/page', data, 'get')
	},
	// 修改前台项目展示信息与推荐
	portalProjectEditShowcase(data) {
		return baseRequest('/portal/project/editShowcase', data, 'post')
	}
}
