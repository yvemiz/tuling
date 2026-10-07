<template>
	<xn-form-container
		title="组队招募详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="招募需求标题">
				{{ formData.title !== undefined && formData.title !== null ? formData.title : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="发布组长">
				{{ formData.leaderId !== undefined && formData.leaderId !== null ? formData.leaderId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="拟招募人数">
				{{ formData.recruitCount !== undefined && formData.recruitCount !== null ? formData.recruitCount : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="招募状态">
				{{ formData.status !== undefined && formData.status !== null ? formData.status : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="截止时间">
				{{ formData.deadline !== undefined && formData.deadline !== null ? formData.deadline : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="技能与要求说明">
				{{ formData.requirementDesc !== undefined && formData.requirementDesc !== null ? formData.requirementDesc : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="projectrecruitDetail">
	import bizProjectRecruitApi from '@/api/biz/bizProjectRecruitApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizProjectRecruitApi.bizProjectRecruitDetail(param).then((data) => {
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
