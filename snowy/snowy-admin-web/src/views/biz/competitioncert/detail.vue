<template>
	<xn-form-container
		title="竞赛证书详情"
		:width="700"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="证书名称">
				{{ formData.certificateName !== undefined && formData.certificateName !== null ? formData.certificateName : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属竞赛">
				{{ formData.competitionId !== undefined && formData.competitionId !== null ? formData.competitionId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属项目">
				{{ formData.projectId !== undefined && formData.projectId !== null ? formData.projectId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="获奖小组成员" :span="2">
				<a-tag color="blue" v-if="formData.studentNames">{{ formData.studentNames }}</a-tag>
				<span v-else>{{ formData.studentId || '-' }}</span>
			</a-descriptions-item>
			<a-descriptions-item label="获奖级别">
				{{ formData.awardLevel !== undefined && formData.awardLevel !== null ? formData.awardLevel : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="获奖日期">
				{{ formData.awardDate !== undefined && formData.awardDate !== null ? formData.awardDate : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="证书图片" :span="2">
				<a-image
					v-if="formData.certificateImage"
					:src="formData.certificateImage"
					:width="160"
					style="border-radius: 6px; border: 1px solid #e8e8e8; object-fit: cover;"
				/>
				<span v-else style="color: #999;">暂无证书图片</span>
			</a-descriptions-item>
			<a-descriptions-item label="认证状态">
				{{ formData.verifyStatus !== undefined && formData.verifyStatus !== null ? formData.verifyStatus : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="认证教师">
				{{ formData.verifyTeacherId !== undefined && formData.verifyTeacherId !== null ? formData.verifyTeacherId : '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="审核评语" :span="2">
				{{ formData.verifyRemark !== undefined && formData.verifyRemark !== null ? formData.verifyRemark : '-' }}
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="competitioncertDetail">
	import bizCompetitionCertApi from '@/api/biz/bizCompetitionCertApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})
	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizCompetitionCertApi.bizCompetitionCertDetail(param).then((data) => {
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
