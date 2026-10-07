<template>
	<xn-form-container
		title="关注动态详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="关注用户">
				{{ formData.userId !== undefined && formData.userId !== null ? formData.userId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="目标类型">
				{{ formData.targetType !== undefined && formData.targetType !== null ? formData.targetType : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="目标ID">
				{{ formData.targetId !== undefined && formData.targetId !== null ? formData.targetId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="关注时间">
				{{ formData.followTime !== undefined && formData.followTime !== null ? formData.followTime : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="followDetail">
	import bizFollowApi from '@/api/biz/bizFollowApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizFollowApi.bizFollowDetail(param).then((data) => {
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
