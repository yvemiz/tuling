<template>
	<xn-form-container
		title="催交记录详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="催交题目">
				{{ formData.taskId !== undefined && formData.taskId !== null ? formData.taskId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="催促组长">
				{{ formData.leaderId !== undefined && formData.leaderId !== null ? formData.leaderId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="被催组员">
				{{ formData.studentId !== undefined && formData.studentId !== null ? formData.studentId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="催促提醒内容">
				{{ formData.urgeMessage !== undefined && formData.urgeMessage !== null ? formData.urgeMessage : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="催促时间">
				{{ formData.urgeTime !== undefined && formData.urgeTime !== null ? formData.urgeTime : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="algorithmurgeDetail">
	import bizAlgorithmUrgeApi from '@/api/biz/bizAlgorithmUrgeApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizAlgorithmUrgeApi.bizAlgorithmUrgeDetail(param).then((data) => {
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
