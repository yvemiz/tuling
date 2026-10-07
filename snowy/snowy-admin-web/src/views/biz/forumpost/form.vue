<template>
	<xn-form-container
		:title="formData.id ? '编辑社区帖子' : '新增社区帖子'"
		:width="750"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="帖子标题：" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入帖子标题" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="发帖人：" name="authorId">
						<a-select v-model:value="formData.authorId" placeholder="请选择或输入用户" show-search allow-clear :filter-option="filterOption">
							<a-select-option v-for="item in userOptions" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="帖子类别：" name="postType">
						<a-select v-model:value="formData.postType" placeholder="请选择帖子类别" allow-clear>
							<a-select-option value="GENERAL">普通交流</a-select-option>
							<a-select-option value="RECRUITMENT">组长招聘</a-select-option>
							<a-select-option value="FOLLOW_EVENT">关注动态</a-select-option>
							<a-select-option value="COMPETITION_EVENT">竞赛发布</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="是否置顶：" name="isPinned">
						<a-radio-group v-model:value="formData.isPinned">
							<a-radio value="NO">普通</a-radio>
							<a-radio value="YES">置顶</a-radio>
						</a-radio-group>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="审核状态：" name="auditStatus">
						<a-select v-model:value="formData.auditStatus" placeholder="请选择审核状态" allow-clear>
							<a-select-option value="AI_PENDING">待AI审查</a-select-option>
							<a-select-option value="PUBLISHED">已发布</a-select-option>
							<a-select-option value="MANUAL_PENDING">转人工审核</a-select-option>
							<a-select-option value="REJECTED">已驳回</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="正文内容：" name="content">
						<a-textarea v-model:value="formData.content" :rows="4" placeholder="请输入正文内容" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="forumpostForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import bizForumPostApi from '@/api/biz/bizForumPostApi'
	import bizCompetitionApi from '@/api/biz/bizCompetitionApi'
	import bizProjectApi from '@/api/biz/bizProjectApi'
	import bizAlgorithmTaskApi from '@/api/biz/bizAlgorithmTaskApi'

	// 抽屉状态
	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)

	// 常用字典与下拉选项
	const collegeOptions = [
		{ label: '计算机科学与技术学院', value: '1548901111999773978' },
		{ label: '软件工程学院', value: '1548901111999773979' },
		{ label: '人工智能学院', value: '1548901111999773980' },
		{ label: '数学与统计学院', value: '1548901111999773981' },
		{ label: '电子信息工程学院', value: '1548901111999773982' }
	]
	const competitionOptions = ref([
		{ label: '2026年全国大学生计算机系统能力大赛', value: '2106631000000000001' },
		{ label: '第十五届蓝桥杯全国软件和信息技术专业人才大赛', value: '2106631000000000002' },
		{ label: '中国大学生计算机设计大赛（4C）', value: '2106631000000000003' },
		{ label: '全国大学生数学建模竞赛（MCM）', value: '2106631000000000004' }
	])
	const projectOptions = ref([
		{ label: '基于大模型的智能代码缺陷自动修复系统团队', value: '2106632000000000001' },
		{ label: '高校智能排课与评教实训系统团队', value: '2106632000000000002' },
		{ label: '分布式实时视觉检测算法战队', value: '2106632000000000003' }
	])
	const taskOptions = ref([
		{ label: '高并发LRU缓存机制工业级设计与实现', value: '2106633000000000001' },
		{ label: '红黑树节点旋转与平衡维护算法深度实训', value: '2106633000000000002' },
		{ label: '多源最短路径Dijkstra算法工业级优化', value: '2106633000000000003' }
	])
	const userOptions = ref([
		{ label: '张教授 (指导教师/工号T1001)', value: '1570687866138206208' },
		{ label: '李老师 (竞赛导师/工号T1002)', value: '1548901111999773979' },
		{ label: '陈组长 (学生队长/学号S2023001)', value: '1813959658013433858' },
		{ label: '王同学 (算法主程/学号S2023002)', value: '1548901111999773980' },
		{ label: '赵同学 (前端开发/学号S2023003)', value: '1548901111999773981' }
	])

	const filterOption = (input, option) => {
		return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
	}

	// 异步加载动态关系实体
	const loadRelationOptions = () => {
		bizCompetitionApi.bizCompetitionPage({ size: 50 }).then((res) => {
			if (res && res.records && res.records.length > 0) {
				competitionOptions.value = res.records.map(r => ({ label: r.title, value: r.id }))
			}
		}).catch(() => {})
		bizProjectApi.bizProjectPage({ size: 50 }).then((res) => {
			if (res && res.records && res.records.length > 0) {
				projectOptions.value = res.records.map(r => ({ label: r.projectName, value: r.id }))
			}
		}).catch(() => {})
		bizAlgorithmTaskApi.bizAlgorithmTaskPage({ size: 50 }).then((res) => {
			if (res && res.records && res.records.length > 0) {
				taskOptions.value = res.records.map(r => ({ label: r.title, value: r.id }))
			}
		}).catch(() => {})
	}

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		loadRelationOptions()
		if (record) {
			let param = {
				id: record.id
			}
			bizForumPostApi.bizForumPostDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
			})
		} else {
			formData.value = {}
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认校验规则
	const formRules = {
	"title": [
		{
			"required": true,
			"message": "请输入或选择帖子标题",
			"trigger": "blur"
		}
	],
	"authorId": [
		{
			"required": true,
			"message": "请输入或选择发帖人",
			"trigger": "blur"
		}
	]
}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			bizForumPostApi
				.bizForumPostSubmitForm(formDataParam, formDataParam.id)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	// 抛出日志
	defineExpose({
		onOpen
	})
</script>
