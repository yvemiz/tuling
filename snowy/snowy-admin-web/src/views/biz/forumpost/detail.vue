<template>
	<xn-form-container
		title="社区帖子详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="帖子标题">
				{{ formData.title !== undefined && formData.title !== null ? formData.title : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="发帖人">
				{{ formData.authorId !== undefined && formData.authorId !== null ? formData.authorId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="帖子类别">
				{{ formData.postType !== undefined && formData.postType !== null ? formData.postType : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="是否置顶">
				{{ formData.isPinned !== undefined && formData.isPinned !== null ? formData.isPinned : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="审核状态">
				{{ formData.auditStatus !== undefined && formData.auditStatus !== null ? formData.auditStatus : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="正文内容" :span="2">
				<div style="white-space: pre-wrap; max-height: 450px; overflow-y: auto; background: #f8fafc; padding: 12px; border-radius: 6px; font-family: monospace;">{{ formData.content !== undefined && formData.content !== null ? formData.content : '-' }}</div>
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="forumpostDetail">
	import bizForumPostApi from '@/api/biz/bizForumPostApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizForumPostApi.bizForumPostDetail(param).then((data) => {
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
