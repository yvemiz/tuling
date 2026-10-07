<template>
	<xn-form-container
		title="学生档案详情"
		:width="750"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<div style="display: flex; align-items: center; gap: 16px; margin-bottom: 20px; padding: 16px; background: #f8fafc; border-radius: 8px;">
			<a-avatar :size="64" :src="formData.avatar" style="background-color: #1890ff">
				{{ formData.name ? formData.name.charAt(0) : '学' }}
			</a-avatar>
			<div>
				<div style="font-size: 18px; font-weight: 700; color: #1e293b;">
					{{ formData.name || '未绑定前台用户' }}
					<a-tag color="blue" style="margin-left: 8px;">{{ formData.grade || '学生' }}</a-tag>
				</div>
				<div style="font-size: 13px; color: #64748b; margin-top: 4px;">
					<span>账号：<strong>{{ formData.account || '-' }}</strong></span>
					<span style="margin: 0 8px;">|</span>
					<span>学号：<strong>{{ formData.studentNo || '-' }}</strong></span>
					<span style="margin: 0 8px;">|</span>
					<span>状态：<a-tag :color="formData.userStatus === 'ENABLE' ? 'green' : 'red'">{{ formData.userStatus === 'ENABLE' ? '正常' : '已禁用' }}</a-tag></span>
				</div>
			</div>
		</div>

		<a-descriptions :column="2" bordered size="small">
			<a-descriptions-item label="学生姓名">
				{{ formData.name || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="登录账号">
				{{ formData.account || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="学号">
				{{ formData.studentNo || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="联系电话">
				{{ formData.phone || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="电子邮箱">
				{{ formData.email || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="所属学院">
				{{ getCollegeName(formData.collegeId) }}
			</a-descriptions-item>
			<a-descriptions-item label="专业名称">
				{{ formData.major || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="年级">
				{{ formData.grade || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="班级名称">
				{{ formData.className || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="注册时间">
				{{ formData.createTime || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="技能特长" :span="2">
				<a-space wrap v-if="formData.skillTags">
					<a-tag color="blue" v-for="tag in formData.skillTags.split(',')" :key="tag">{{ tag.trim() }}</a-tag>
				</a-space>
				<span v-else>-</span>
			</a-descriptions-item>
			<a-descriptions-item label="个人简介" :span="2">
				<div style="white-space: pre-wrap; line-height: 1.6;">{{ formData.bio || '暂无个人简介' }}</div>
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="studentprofileDetail">
	import bizStudentProfileApi from '@/api/biz/bizStudentProfileApi'
	// 默认是关闭状态
	const open = ref(false)
	const formData = ref({})

	const getCollegeName = (id) => {
		const map = {
			'1548901111999773978': '计算机科学与技术学院',
			'1548901111999773979': '软件工程学院',
			'1548901111999773980': '人工智能学院',
			'1548901111999773981': '数学与统计学院',
			'1548901111999773982': '电子信息工程学院'
		}
		return map[id] || id || '-'
	}

	// 打开抽屉
	const onOpen = (record) => {
		open.value = true
		let param = {
			id: record.id
		}
		bizStudentProfileApi.bizStudentProfileDetail(param).then((data) => {
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
