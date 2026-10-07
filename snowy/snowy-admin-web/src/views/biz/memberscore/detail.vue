<template>
	<xn-form-container
		title="组员打分详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="所属项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="被评组员">
				{{ formData.studentId !== undefined && formData.studentId !== null ? formData.studentId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="打分人">
				{{ formData.evaluatorId !== undefined && formData.evaluatorId !== null ? formData.evaluatorId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="打分人身份">
				{{ formData.evaluatorRole !== undefined && formData.evaluatorRole !== null ? formData.evaluatorRole : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="表现评分 (0-100)">
				{{ formData.score !== undefined && formData.score !== null ? formData.score : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="考核周期/阶段">
				{{ formData.scorePeriod !== undefined && formData.scorePeriod !== null ? formData.scorePeriod : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="保密评语">
				{{ formData.remark !== undefined && formData.remark !== null ? formData.remark : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="memberscoreDetail">
	import bizMemberScoreApi from '@/api/biz/bizMemberScoreApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizMemberScoreApi.bizMemberScoreDetail(param).then((data) => {
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
