<template>
	<xn-form-container
		title="帖子审核详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="审核帖子ID">
				{{ formData.postId !== undefined && formData.postId !== null ? formData.postId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="审核主体">
				{{ formData.auditorType !== undefined && formData.auditorType !== null ? formData.auditorType : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="审核人用户">
				{{ formData.auditorId !== undefined && formData.auditorId !== null ? formData.auditorId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="审核决议">
				{{ formData.auditResult !== undefined && formData.auditResult !== null ? formData.auditResult : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="审核说明理由">
				{{ formData.auditReason !== undefined && formData.auditReason !== null ? formData.auditReason : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="forumauditDetail">
	import bizForumAuditApi from '@/api/biz/bizForumAuditApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizForumAuditApi.bizForumAuditDetail(param).then((data) => {
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
