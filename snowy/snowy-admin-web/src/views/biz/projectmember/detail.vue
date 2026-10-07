<template>
	<xn-form-container
		title="团队成员详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="所属项目组">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="成员学生">
				{{ formData.studentId !== undefined && formData.studentId !== null ? formData.studentId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="团队内角色">
				{{ formData.memberRole !== undefined && formData.memberRole !== null ? formData.memberRole : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="在组状态">
				{{ formData.memberStatus !== undefined && formData.memberStatus !== null ? formData.memberStatus : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="组内分工职责">
				{{ formData.dutyRemark !== undefined && formData.dutyRemark !== null ? formData.dutyRemark : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="入组时间">
				{{ formData.joinTime !== undefined && formData.joinTime !== null ? formData.joinTime : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="projectmemberDetail">
	import bizProjectMemberApi from '@/api/biz/bizProjectMemberApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizProjectMemberApi.bizProjectMemberDetail(param).then((data) => {
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
