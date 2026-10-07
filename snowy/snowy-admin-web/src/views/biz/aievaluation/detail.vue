<template>
	<xn-form-container
		title="团队AI诊断详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="评估项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="触发评估人">
				{{ formData.triggerUserId !== undefined && formData.triggerUserId !== null ? formData.triggerUserId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="评估批次/阶段">
				{{ formData.evaluationBatch !== undefined && formData.evaluationBatch !== null ? formData.evaluationBatch : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="算法完成率 (%)">
				{{ formData.algorithmCompletionRate !== undefined && formData.algorithmCompletionRate !== null ? formData.algorithmCompletionRate : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="活跃度得分">
				{{ formData.competitionParticipationScore !== undefined && formData.competitionParticipationScore !== null ? formData.competitionParticipationScore : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="AI综合得分">
				{{ formData.aiTotalScore !== undefined && formData.aiTotalScore !== null ? formData.aiTotalScore : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="AI诊断报告详情">
				{{ formData.aiEvaluationReport !== undefined && formData.aiEvaluationReport !== null ? formData.aiEvaluationReport : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="aievaluationDetail">
	import bizAiEvaluationApi from '@/api/biz/bizAiEvaluationApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizAiEvaluationApi.bizAiEvaluationDetail(param).then((data) => {
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
