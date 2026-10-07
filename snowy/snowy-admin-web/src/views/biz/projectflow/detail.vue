<template>
	<xn-form-container
		title="入组流转详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="申请项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="涉及学生">
				{{ formData.studentId !== undefined && formData.studentId !== null ? formData.studentId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="流转方向">
				{{ formData.direction !== undefined && formData.direction !== null ? formData.direction : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="流转审批状态">
				{{ formData.status !== undefined && formData.status !== null ? formData.status : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="附言理由">
				{{ formData.message !== undefined && formData.message !== null ? formData.message : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="处理反馈备注">
				{{ formData.handleRemark !== undefined && formData.handleRemark !== null ? formData.handleRemark : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="projectflowDetail">
	import bizProjectFlowApi from '@/api/biz/bizProjectFlowApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizProjectFlowApi.bizProjectFlowDetail(param).then((data) => {
			formData.value = Object.assign({}, data)
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formData.value = {}
		open.value = false
	}
	defineExpose({
		onOpen
	})
</script>
