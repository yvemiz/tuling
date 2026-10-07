<template>
	<xn-form-container
		title="教师档案详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="教师工号">
				{{ formData.teacherNo !== undefined && formData.teacherNo !== null ? formData.teacherNo : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="关联用户">
				{{ formData.userId !== undefined && formData.userId !== null ? formData.userId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属学院">
				{{ formData.collegeId !== undefined && formData.collegeId !== null ? formData.collegeId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="职称">
				{{ formData.title !== undefined && formData.title !== null ? formData.title : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="研究方向">
				{{ formData.researchDirection !== undefined && formData.researchDirection !== null ? formData.researchDirection : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="教师简介">
				{{ formData.bio !== undefined && formData.bio !== null ? formData.bio : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="teacherprofileDetail">
	import bizTeacherProfileApi from '@/api/biz/bizTeacherProfileApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizTeacherProfileApi.bizTeacherProfileDetail(param).then((data) => {
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
