<template>
	<xn-form-container
		:title="formData.id ? '编辑项目团队' : '新增项目团队'"
		:width="750"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="项目团队名称：" name="projectName">
						<a-input v-model:value="formData.projectName" placeholder="请输入项目团队名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="项目组长：" name="leaderId">
						<a-select v-model:value="formData.leaderId" placeholder="请选择或输入用户" show-search allow-clear :filter-option="filterOption">
							<a-select-option v-for="item in userOptions" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="是否私密保护：" name="isPrivate">
						<a-radio-group v-model:value="formData.isPrivate">
							<a-radio value="NO">公开团队</a-radio>
							<a-radio value="YES">私密团队</a-radio>
						</a-radio-group>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="组队状态：" name="status">
						<a-select v-model:value="formData.status" placeholder="请选择组队状态" allow-clear>
							<a-select-option value="RECRUITING">招募中</a-select-option>
							<a-select-option value="FORMULATED">组队完成</a-select-option>
							<a-select-option value="ARCHIVED">已归档</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="团队成员上限：" name="maxMemberLimit">
						<a-input-number v-model:value="formData.maxMemberLimit" :min="1" :max="20" placeholder="请输入数值" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="当前成员数：" name="currentMemberCount">
						<a-input-number v-model:value="formData.currentMemberCount" :min="1" :max="20" placeholder="请输入数值" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="项目介绍与技术规划：" name="projectDesc">
						<a-textarea v-model:value="formData.projectDesc" :rows="4" placeholder="请输入项目介绍与技术规划" allow-clear />
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

<script setup name="projectForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import bizProjectApi from '@/api/biz/bizProjectApi'
	import bizCompetitionApi from '@/api/biz/bizCompetitionApi'
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
			bizProjectApi.bizProjectDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
			})
		} else {
			formData.value = {
			"isPrivate": "NO",
			"status": "RECRUITING"
}
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
	"projectName": [
		{
			"required": true,
			"message": "请输入或选择项目团队名称",
			"trigger": "blur"
		}
	],
	"leaderId": [
		{
			"required": true,
			"message": "请输入或选择项目组长",
			"trigger": "blur"
		}
	]
}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			bizProjectApi
				.bizProjectSubmitForm(formDataParam, formDataParam.id)
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
