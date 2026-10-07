<template>
	<xn-form-container
		:title="formData.id ? '编辑竞赛信息' : '新增竞赛信息'"
		:width="750"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="竞赛名称：" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入竞赛名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="竞赛级别：" name="competitionLevel">
						<a-select v-model:value="formData.competitionLevel" placeholder="请选择竞赛级别" allow-clear>
							<a-select-option value="SCHOOL">校级</a-select-option>
							<a-select-option value="PROVINCE">省级</a-select-option>
							<a-select-option value="NATIONAL">国家级</a-select-option>
							<a-select-option value="INTERNATIONAL">国际级</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="主办/承办单位：" name="organizer">
						<a-input v-model:value="formData.organizer" placeholder="请输入主办/承办单位" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="竞赛类别：" name="category">
						<a-select v-model:value="formData.category" placeholder="请选择竞赛类别" allow-clear>
							<a-select-option value="算法与程序设计">算法与程序设计</a-select-option>
							<a-select-option value="软件创新与开发">软件创新与开发</a-select-option>
							<a-select-option value="数学建模">数学建模</a-select-option>
							<a-select-option value="人工智能与大数据">人工智能与大数据</a-select-option>
							<a-select-option value="物联网与创客">物联网与创客</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="报名开始时间：" name="signupStartTime">
						<a-date-picker v-model:value="formData.signupStartTime" show-time value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="报名截止时间：" name="signupEndTime">
						<a-date-picker v-model:value="formData.signupEndTime" show-time value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="比赛开始时间：" name="competitionStartTime">
						<a-date-picker v-model:value="formData.competitionStartTime" show-time value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="比赛结束时间：" name="competitionEndTime">
						<a-date-picker v-model:value="formData.competitionEndTime" show-time value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="最少参赛人数：" name="minMembers">
						<a-input-number v-model:value="formData.minMembers" :min="1" :max="10" placeholder="请输入数值" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="最多参赛人数 (1为单人赛)：" name="maxMembers">
						<a-input-number v-model:value="formData.maxMembers" :min="1" :max="20" placeholder="请输入数值" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="竞赛状态：" name="status">
						<a-select v-model:value="formData.status" placeholder="请选择竞赛状态" allow-clear>
							<a-select-option value="PENDING">未开始</a-select-option>
							<a-select-option value="SIGNUP">报名中</a-select-option>
							<a-select-option value="ONGOING">进行中</a-select-option>
							<a-select-option value="REVIEWING">评审中</a-select-option>
							<a-select-option value="FINISHED">已结束</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="发布教师：" name="publishTeacherId">
						<a-select v-model:value="formData.publishTeacherId" placeholder="请选择或输入用户" show-search allow-clear :filter-option="filterOption">
							<a-select-option v-for="item in userOptions" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="竞赛要求与参赛规程：" name="requirements">
						<a-textarea v-model:value="formData.requirements" :rows="4" placeholder="请输入竞赛要求与参赛规程" allow-clear />
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

<script setup name="competitionForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
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
			bizCompetitionApi.bizCompetitionDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
			})
		} else {
			formData.value = {
			"minMembers": 1,
			"maxMembers": 5,
			"status": "PENDING"
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
	"title": [
		{
			"required": true,
			"message": "请输入或选择竞赛名称",
			"trigger": "blur"
		}
	],
	"publishTeacherId": [
		{
			"required": true,
			"message": "请输入或选择发布教师",
			"trigger": "blur"
		}
	]
}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			bizCompetitionApi
				.bizCompetitionSubmitForm(formDataParam, formDataParam.id)
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
