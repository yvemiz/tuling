<template>
	<xn-form-container
		title="项目团队详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="项目团队名称">
				{{ formData.projectName !== undefined && formData.projectName !== null ? formData.projectName : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="项目组长">
				{{ formData.leaderId !== undefined && formData.leaderId !== null ? formData.leaderId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="是否私密保护">
				{{ formData.isPrivate !== undefined && formData.isPrivate !== null ? formData.isPrivate : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="组队状态">
				{{ formData.status !== undefined && formData.status !== null ? formData.status : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="团队成员上限">
				{{ formData.maxMemberLimit !== undefined && formData.maxMemberLimit !== null ? formData.maxMemberLimit : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="当前成员数">
				{{ formData.currentMemberCount !== undefined && formData.currentMemberCount !== null ? formData.currentMemberCount : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="项目介绍与技术规划">
				{{ formData.projectDesc !== undefined && formData.projectDesc !== null ? formData.projectDesc : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="projectDetail">
	import bizProjectApi from '@/api/biz/bizProjectApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizProjectApi.bizProjectDetail(param).then((data) => {
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
