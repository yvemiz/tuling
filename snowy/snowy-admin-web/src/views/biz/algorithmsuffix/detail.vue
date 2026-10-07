<template>
	<xn-form-container
		title="作业提交详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="对应算法题目">
				{{ formData.taskId !== undefined && formData.taskId !== null ? formData.taskId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="提交学生">
				{{ formData.studentId !== undefined && formData.studentId !== null ? formData.studentId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="提交形式">
				{{ formData.submissionType !== undefined && formData.submissionType !== null ? formData.submissionType : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="编程语言">
				{{ formData.codeLanguage !== undefined && formData.codeLanguage !== null ? formData.codeLanguage : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="批改得分 (0-100)">
				{{ formData.score !== undefined && formData.score !== null ? formData.score : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="作业状态">
				{{ formData.status !== undefined && formData.status !== null ? formData.status : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="代码文本内容">
				{{ formData.codeContent !== undefined && formData.codeContent !== null ? formData.codeContent : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="教师批改评语">
				{{ formData.teacherFeedback !== undefined && formData.teacherFeedback !== null ? formData.teacherFeedback : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="algorithmsuffixDetail">
	import bizAlgorithmSubmissionApi from '@/api/biz/bizAlgorithmSubmissionApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizAlgorithmSubmissionApi.bizAlgorithmSubmissionDetail(param).then((data) => {
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
