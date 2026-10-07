<template>
	<xn-form-container
		title="竞赛信息详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="竞赛名称">
				{{ formData.title !== undefined && formData.title !== null ? formData.title : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="竞赛级别">
				{{ formData.competitionLevel !== undefined && formData.competitionLevel !== null ? formData.competitionLevel : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="主办/承办单位">
				{{ formData.organizer !== undefined && formData.organizer !== null ? formData.organizer : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="竞赛类别">
				{{ formData.category !== undefined && formData.category !== null ? formData.category : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="报名开始时间">
				{{ formData.signupStartTime !== undefined && formData.signupStartTime !== null ? formData.signupStartTime : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="报名截止时间">
				{{ formData.signupEndTime !== undefined && formData.signupEndTime !== null ? formData.signupEndTime : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="比赛开始时间">
				{{ formData.competitionStartTime !== undefined && formData.competitionStartTime !== null ? formData.competitionStartTime : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="比赛结束时间">
				{{ formData.competitionEndTime !== undefined && formData.competitionEndTime !== null ? formData.competitionEndTime : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="最少参赛人数">
				{{ formData.minMembers !== undefined && formData.minMembers !== null ? formData.minMembers : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="最多参赛人数 (1为单人赛)">
				{{ formData.maxMembers !== undefined && formData.maxMembers !== null ? formData.maxMembers : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="竞赛状态">
				{{ formData.status !== undefined && formData.status !== null ? formData.status : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="发布教师">
				{{ formData.publishTeacherId !== undefined && formData.publishTeacherId !== null ? formData.publishTeacherId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="竞赛要求与参赛规程">
				{{ formData.requirements !== undefined && formData.requirements !== null ? formData.requirements : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="competitionDetail">
	import bizCompetitionApi from '@/api/biz/bizCompetitionApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizCompetitionApi.bizCompetitionDetail(param).then((data) => {
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
