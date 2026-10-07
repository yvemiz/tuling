<template>
	<xn-form-container
		title="项目排行详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="排名名次">
				{{ formData.rankPosition !== undefined && formData.rankPosition !== null ? formData.rankPosition : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="上榜项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属竞赛">
				{{ formData.competitionId !== undefined && formData.competitionId !== null ? formData.competitionId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="榜单周期">
				{{ formData.rankingPeriod !== undefined && formData.rankingPeriod !== null ? formData.rankingPeriod : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="教师评分 (0-100)">
				{{ formData.teacherScore !== undefined && formData.teacherScore !== null ? formData.teacherScore : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="AI评分 (0-100)">
				{{ formData.aiScore !== undefined && formData.aiScore !== null ? formData.aiScore : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="最终总得分 (0-100)">
				{{ formData.finalScore !== undefined && formData.finalScore !== null ? formData.finalScore : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="是否发布上榜">
				{{ formData.isPublished !== undefined && formData.isPublished !== null ? formData.isPublished : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="发布教师">
				{{ formData.publishTeacherId !== undefined && formData.publishTeacherId !== null ? formData.publishTeacherId : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="projectrankingDetail">
	import bizProjectRankingApi from '@/api/biz/bizProjectRankingApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizProjectRankingApi.bizProjectRankingDetail(param).then((data) => {
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
