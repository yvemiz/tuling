<template>
	<xn-form-container
		title="任务指派详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="算法题目">
				{{ formData.taskId !== undefined && formData.taskId !== null ? formData.taskId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="指派项目团队">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="指派时间">
				{{ formData.assignTime !== undefined && formData.assignTime !== null ? formData.assignTime : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="algorithmtaskprojDetail">
	import bizAlgorithmTaskProjApi from '@/api/biz/bizAlgorithmTaskProjApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizAlgorithmTaskProjApi.bizAlgorithmTaskProjDetail(param).then((data) => {
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
