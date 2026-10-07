<template>
	<xn-form-container
		:title="formData.id ? '编辑学生档案' : '新增学生档案'"
		:width="750"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="关联C端用户：" name="userId">
						<a-select
							v-model:value="formData.userId"
							placeholder="请选择关联前台用户"
							show-search
							allow-clear
							:filter-option="filterOption"
							@change="handleUserChange"
						>
							<a-select-option v-for="item in userOptions" :key="item.value" :value="item.value">
								{{ item.label }}
							</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="学号：" name="studentNo">
						<a-input v-model:value="formData.studentNo" placeholder="请输入学号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="学生姓名：" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入学生姓名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="登录账号：" name="account">
						<a-input v-model:value="formData.account" placeholder="请输入前台登录账号" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="联系电话：" name="phone">
						<a-input v-model:value="formData.phone" placeholder="请输入手机号码" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="电子邮箱：" name="email">
						<a-input v-model:value="formData.email" placeholder="请输入电子邮箱" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="所属学院：" name="collegeId">
						<a-select v-model:value="formData.collegeId" placeholder="请选择所属学院" allow-clear>
							<a-select-option v-for="c in collegeOptions" :key="c.value" :value="c.value">{{ c.label }}</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="专业名称：" name="major">
						<a-input v-model:value="formData.major" placeholder="请输入专业名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="年级：" name="grade">
						<a-select v-model:value="formData.grade" placeholder="请选择年级" allow-clear>
							<a-select-option value="2021级">2021级</a-select-option>
							<a-select-option value="2022级">2022级</a-select-option>
							<a-select-option value="2023级">2023级</a-select-option>
							<a-select-option value="2024级">2024级</a-select-option>
							<a-select-option value="2025级">2025级</a-select-option>
							<a-select-option value="2026级">2026级</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="12" :lg="12">
					<a-form-item label="班级名称：" name="className">
						<a-input v-model:value="formData.className" placeholder="请输入班级名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24">
					<a-form-item label="技能特长标签：" name="skillTags">
						<a-input v-model:value="formData.skillTags" placeholder="多个标签用逗号分隔，如: Java,Python,算法,前端" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="24">
					<a-form-item label="个人简介：" name="bio">
						<a-textarea v-model:value="formData.bio" :rows="4" placeholder="请输入个人简介" allow-clear />
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

<script setup name="studentprofileForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import bizStudentProfileApi from '@/api/biz/bizStudentProfileApi'
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
	const userOptions = ref([])
	const rawUserList = ref([])

	const filterOption = (input, option) => {
		return (option.label || '').toLowerCase().indexOf((input || '').toLowerCase()) >= 0
	}

	// 加载真实C端用户列表
	const loadUserOptions = () => {
		clientUserApi.userPage({ size: 200 }).then((res) => {
			if (res && res.records) {
				rawUserList.value = res.records
				userOptions.value = res.records.map((u) => ({
					label: `${u.name || '未命名'} (${u.account || '-'} / ${u.phone || '无手机'})`,
					value: u.id,
					data: u
				}))
			}
		}).catch(() => {})
	}

	// 选择关联用户时自动补全
	const handleUserChange = (val) => {
		if (!val) return
		const found = rawUserList.value.find((u) => u.id === val)
		if (found) {
			if (!formData.value.name) formData.value.name = found.name
			if (!formData.value.account) formData.value.account = found.account
			if (!formData.value.phone) formData.value.phone = found.phone
			if (!formData.value.email) formData.value.email = found.email
		}
	}

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		loadUserOptions()
		if (record) {
			let param = {
				id: record.id
			}
			bizStudentProfileApi.bizStudentProfileDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
			})
		} else {
			formData.value = {
				grade: '2023级',
				collegeId: '1548901111999773979'
			}
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	// 默认要校验的
	const formRules = {
		studentNo: [required('请输入学号')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				const formParam = cloneDeep(formData.value)
				bizStudentProfileApi
					.bizStudentProfileSubmitForm(formParam, !formParam.id)
					.then(() => {
						onClose()
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>
