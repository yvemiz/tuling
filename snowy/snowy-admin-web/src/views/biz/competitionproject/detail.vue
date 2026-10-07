<template>
	<xn-form-container
		title="竞赛报名详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="所属竞赛">
				{{ formData.competitionId !== undefined && formData.competitionId !== null ? formData.competitionId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="参赛形式">
				{{ formData.signupType !== undefined && formData.signupType !== null ? formData.signupType : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="参赛项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="参赛学生 (个人参赛)">
				{{ formData.studentId !== undefined && formData.studentId !== null ? formData.studentId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="指导老师">
				{{ formData.instructorTeacherId !== undefined && formData.instructorTeacherId !== null ? formData.instructorTeacherId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="资格审核状态">
				{{ formData.auditStatus !== undefined && formData.auditStatus !== null ? formData.auditStatus : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="最终赛果/名次">
				{{ formData.competitionResult !== undefined && formData.competitionResult !== null ? formData.competitionResult : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="报名时间">
				{{ formData.enrollTime !== undefined && formData.enrollTime !== null ? formData.enrollTime : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="competitionprojectDetail">
	import bizCompetitionProjectApi from '@/api/biz/bizCompetitionProjectApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizCompetitionProjectApi.bizCompetitionProjectDetail(param).then((data) => {
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
