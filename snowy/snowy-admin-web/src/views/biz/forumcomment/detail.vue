<template>
	<xn-form-container
		title="社区评论详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="所属帖子ID">
				{{ formData.postId !== undefined && formData.postId !== null ? formData.postId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="评论用户">
				{{ formData.userId !== undefined && formData.userId !== null ? formData.userId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="评论内容">
				{{ formData.content !== undefined && formData.content !== null ? formData.content : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="forumcommentDetail">
	import bizForumCommentApi from '@/api/biz/bizForumCommentApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizForumCommentApi.bizForumCommentDetail(param).then((data) => {
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
