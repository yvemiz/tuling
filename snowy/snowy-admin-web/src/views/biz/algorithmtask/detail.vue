<template>
	<xn-form-container
		title="算法题库详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="算法练习题目名称">
				{{ formData.title !== undefined && formData.title !== null ? formData.title : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="题目难度">
				{{ formData.difficulty !== undefined && formData.difficulty !== null ? formData.difficulty : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="允许提交形式">
				{{ formData.allowedSubmissionTypes !== undefined && formData.allowedSubmissionTypes !== null ? formData.allowedSubmissionTypes : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="截止提交时间">
				{{ formData.deadline !== undefined && formData.deadline !== null ? formData.deadline : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="出题教师">
				{{ formData.teacherId !== undefined && formData.teacherId !== null ? formData.teacherId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="题目描述与任务规程">
				{{ formData.contentDesc !== undefined && formData.contentDesc !== null ? formData.contentDesc : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="输入样例">
				{{ formData.sampleInput !== undefined && formData.sampleInput !== null ? formData.sampleInput : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="输出样例">
				{{ formData.sampleOutput !== undefined && formData.sampleOutput !== null ? formData.sampleOutput : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="algorithmtaskDetail">
	import bizAlgorithmTaskApi from '@/api/biz/bizAlgorithmTaskApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizAlgorithmTaskApi.bizAlgorithmTaskDetail(param).then((data) => {
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
