<template>
	<xn-form-container
		:title="formData.id ? '编辑教师档案' : '新增教师档案'"
		:width="750"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="教师工号：" name="teacherNo">
						<a-input v-model:value="formData.teacherNo" placeholder="请输入教师工号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="关联用户：" name="userId">
						<a-select v-model:value="formData.userId" placeholder="请选择或输入用户" show-search allow-clear :filter-option="filterOption">
							<a-select-option v-for="item in userOptions" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="所属学院：" name="collegeId">
						<a-select v-model:value="formData.collegeId" placeholder="请选择所属学院" allow-clear>
							<a-select-option v-for="c in collegeOptions" :key="c.value" :value="c.value">{{ c.label }}</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="职称：" name="title">
						<a-select v-model:value="formData.title" placeholder="请选择职称" allow-clear>
							<a-select-option value="教授">教授</a-select-option>
							<a-select-option value="副教授">副教授</a-select-option>
							<a-select-option value="讲师">讲师</a-select-option>
							<a-select-option value="助教">助教</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="研究方向：" name="researchDirection">
						<a-input v-model:value="formData.researchDirection" placeholder="请输入研究方向" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
					<a-form-item label="教师简介：" name="bio">
						<a-textarea v-model:value="formData.bio" :rows="4" placeholder="请输入教师简介" allow-clear />
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

<script setup name="teacherprofileForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import bizTeacherProfileApi from '@/api/biz/bizTeacherProfileApi'
	import bizCompetitionApi from '@/api/biz/bizCompetitionApi'
	import bizProjectApi from '@/api/biz/bizProjectApi'
	import bizAlgorithmTaskApi from '@/api/biz/bizAlgorithmTaskApi'
	import clientUserApi from '@/api/client/clientUserApi'

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
	const competitionOptions = ref([])
	const projectOptions = ref([])
	const taskOptions = ref([])
	const userOptions = ref([])

	const filterOption = (input, option) => {
		return (option.label || '').toLowerCase().indexOf((input || '').toLowerCase()) >= 0
	}

	// 异步加载动态关系实体
	const loadRelationOptions = () => {
		clientUserApi.userPage({ size: 200 }).then((res) => {
			if (res && res.records) {
				userOptions.value = res.records.map(u => ({
					label: `${u.name || '未命名'} (${u.account || '-'} / ${u.phone || '无手机'})`,
					value: u.id
				}))
			}
		}).catch(() => {})
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
			bizTeacherProfileApi.bizTeacherProfileDetail(param).then((data) => {
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
	"teacherNo": [
		{
			"required": true,
			"message": "请输入或选择教师工号",
			"trigger": "blur"
		}
	],
	"userId": [
		{
			"required": true,
			"message": "请输入或选择关联用户",
			"trigger": "blur"
		}
	]
}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			bizTeacherProfileApi
				.bizTeacherProfileSubmitForm(formDataParam, formDataParam.id)
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
