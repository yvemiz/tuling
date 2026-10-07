<template>
	<xn-form-container
		title="编辑前台项目展示与推荐"
		:width="650"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="前台项目展示名称：" name="projectName" required>
						<a-input v-model:value="formData.projectName" placeholder="请输入前台展示的项目名称" allow-clear />
					</a-form-item>
				</a-col>

				<a-col :span="24">
					<a-form-item label="前台项目封面图片链接 (URL)：" name="coverUrl">
						<a-input v-model:value="formData.coverUrl" placeholder="输入在线图片 URL，如 https://... 或留空使用默认封面" allow-clear />
					</a-form-item>
					<div v-if="formData.coverUrl" style="margin-bottom: 16px; text-align: center">
						<img :src="formData.coverUrl" alt="封面预览" style="max-height: 140px; border-radius: 8px; border: 1px solid #e2e8f0; object-fit: cover; max-width: 100%" />
					</div>
				</a-col>

				<a-col :span="12">
					<a-form-item label="首页精选推荐：" name="isFeatured">
						<a-radio-group v-model:value="formData.isFeatured">
							<a-radio value="YES">🌟 推荐至前台首页</a-radio>
							<a-radio value="NO">普通展示</a-radio>
						</a-radio-group>
					</a-form-item>
				</a-col>

				<a-col :span="12">
					<a-form-item label="置顶排序：" name="isPinned">
						<a-radio-group v-model:value="formData.isPinned">
							<a-radio value="YES">📌 置顶团队</a-radio>
							<a-radio value="NO">默认排序</a-radio>
						</a-radio-group>
					</a-form-item>
				</a-col>

				<a-col :span="12">
					<a-form-item label="展示排序权重 (数值越大越靠前)：" name="sortCode">
						<a-input-number v-model:value="formData.sortCode" style="width: 100%" :min="0" />
					</a-form-item>
				</a-col>

				<a-col :span="12">
					<a-form-item label="前台角标文字 (如“重点孵化”、“国奖战队”)：" name="badgeTag">
						<a-input v-model:value="formData.badgeTag" placeholder="输入前台展示角标标签" allow-clear />
					</a-form-item>
				</a-col>

				<a-col :span="24">
					<a-form-item label="前台项目简介描述：" name="projectDesc">
						<a-textarea v-model:value="formData.projectDesc" :rows="3" placeholder="前台项目卡片与详情介绍文案" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">取消</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存修改</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="portalProjectForm">
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
		projectName: [required('请输入项目名称')]
	}

	const onOpen = (record) => {
		open.value = true
		formData.value = cloneDeep(record)
		if (!formData.value.isFeatured) formData.value.isFeatured = 'NO'
		if (!formData.value.isPinned) formData.value.isPinned = 'NO'
		if (!formData.value.sortCode) formData.value.sortCode = 0
	}

	const onClose = () => {
		formRef.value?.resetFields()
		formData.value = {}
		open.value = false
	}

	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const param = {
				projectId: formData.value.id,
				projectName: formData.value.projectName,
				projectDesc: formData.value.projectDesc,
				coverUrl: formData.value.coverUrl,
				isFeatured: formData.value.isFeatured,
				isPinned: formData.value.isPinned,
				sortCode: formData.value.sortCode,
				badgeTag: formData.value.badgeTag
			}
			portalMgrApi
				.portalProjectEditShowcase(param)
				.then(() => {
					message.success('前台项目展示信息修改成功！')
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
