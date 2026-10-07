<template>
	<xn-form-container
		:title="formData.id ? '编辑社区板块' : '新增社区板块'"
		:width="600"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="板块名称：" name="boardName">
						<a-input v-model:value="formData.boardName" placeholder="如：纯算法讨论、求职内推" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="板块编码：" name="boardCode">
						<a-input v-model:value="formData.boardCode" placeholder="如：GENERAL、JOB_SHARE" :disabled="!!formData.id" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="板块图标 / Emoji：" name="icon">
						<a-input v-model:value="formData.icon" placeholder="输入 Emoji 如 💡 或 📢" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="主题颜色：" name="color">
						<a-select v-model:value="formData.color" placeholder="选择标签颜色">
							<a-select-option value="blue">蓝色 (Blue)</a-select-option>
							<a-select-option value="green">绿色 (Green)</a-select-option>
							<a-select-option value="orange">橙色 (Orange)</a-select-option>
							<a-select-option value="purple">紫色 (Purple)</a-select-option>
							<a-select-option value="cyan">青色 (Cyan)</a-select-option>
							<a-select-option value="volcano">火山红 (Volcano)</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="发帖权限限制：" name="postPermission">
						<a-select v-model:value="formData.postPermission">
							<a-select-option value="ALL">🌐 全体师生均可发帖</a-select-option>
							<a-select-option value="LEADER">👑 仅限项目组长与教师</a-select-option>
							<a-select-option value="TEACHER">👨‍🏫 仅限指导教师发布</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码 (数字越小越靠前)：" name="sortCode">
						<a-input-number v-model:value="formData.sortCode" style="width: 100%" :min="0" />
					</a-form-item>
				</a-col>
				<a-col :span="24">
					<a-form-item label="板块简介说明：" name="description">
						<a-textarea v-model:value="formData.description" :rows="3" placeholder="简要描述该板块的交流定位与发帖要求" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">取消</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="portalBoardForm">
	import { ref } from 'vue'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import portalMgrApi from '@/api/portal/portalMgrApi'

	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)

	const formRules = {
		boardName: [required('请输入板块名称')],
		boardCode: [required('请输入板块唯一编码')]
	}

	const onOpen = (record) => {
		open.value = true
		if (record) {
			formData.value = cloneDeep(record)
		} else {
			formData.value = {
				icon: '💡',
				color: 'blue',
				postPermission: 'ALL',
				status: 'ENABLE',
				sortCode: 10
			}
		}
	}

	const onClose = () => {
		formRef.value?.resetFields()
		formData.value = {}
		open.value = false
	}

	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const isEdit = !!formData.value.id
			const apiCall = isEdit ? portalMgrApi.portalBoardEdit(formData.value) : portalMgrApi.portalBoardAdd(formData.value)
			apiCall
				.then(() => {
					message.success(isEdit ? '板块更新成功' : '新增板块成功')
					onClose()
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}

	defineExpose({
		onOpen
	})
</script>
