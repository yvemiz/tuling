<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState" style="margin-bottom: 16px">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="12" :md="8" :lg="6">
					<a-form-item name="searchKey" label="项目名称">
						<a-input v-model:value="searchFormState.searchKey" placeholder="输入项目名称/简介" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="12" :md="8" :lg="6">
					<a-form-item name="isFeatured" label="精选推荐">
						<a-select v-model:value="searchFormState.isFeatured" placeholder="筛选是否精选" allow-clear>
							<a-select-option value="YES">🌟 仅看首页精选</a-select-option>
							<a-select-option value="NO">非精选项目</a-select-option>
						</a-select>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="12" :md="8" :lg="6">
					<a-space>
						<a-button type="primary" @click="tableRef.refresh(true)">
							<template #icon><SearchOutlined /></template>
							查询
						</a-button>
						<a-button @click="reset">
							<template #icon><redo-outlined /></template>
							重置
						</a-button>
					</a-space>
				</a-col>
			</a-row>
		</a-form>

		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:scroll="{ x: 'max-content' }"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'coverUrl'">
					<div class="thumb-box" v-if="record.coverUrl">
						<img :src="record.coverUrl" alt="封面" class="thumb-img" />
					</div>
					<a-avatar v-else shape="square" :size="48" style="background-color: #3b82f6">
						{{ (record.projectName || 'P').charAt(0) }}
					</a-avatar>
				</template>

				<template v-if="column.dataIndex === 'projectName'">
					<div style="font-weight: 600; font-size: 14px">
						{{ record.projectName }}
						<a-tag v-if="record.badgeTag" color="purple" style="margin-left: 6px">
							{{ record.badgeTag }}
						</a-tag>
					</div>
					<div style="color: #64748b; font-size: 12px; margin-top: 2px" class="text-ellipsis">
						{{ record.projectDesc || '暂无描述' }}
					</div>
				</template>

				<template v-if="column.dataIndex === 'leaderName'">
					<span>👑 {{ record.leaderName || '未指定' }}</span>
				</template>

				<template v-if="column.dataIndex === 'isFeatured'">
					<a-tag :color="record.isFeatured === 'YES' ? 'gold' : 'default'">
						{{ record.isFeatured === 'YES' ? '🌟 首页精选' : '普通展示' }}
					</a-tag>
				</template>

				<template v-if="column.dataIndex === 'isPinned'">
					<a-tag :color="record.isPinned === 'YES' ? 'red' : 'default'">
						{{ record.isPinned === 'YES' ? '📌 置顶' : '普通' }}
					</a-tag>
				</template>

				<template v-if="column.dataIndex === 'currentMemberCount'">
					<span>👥 {{ record.currentMemberCount || 1 }} / {{ record.maxMemberLimit || 5 }} 人</span>
				</template>

				<template v-if="column.dataIndex === 'action'">
					<a @click="formRef.onOpen(record)">编辑前台展示</a>
				</template>
			</template>
		</s-table>
	</xn-panel>

	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="portalProjectShowcase">
	import { ref } from 'vue'
	import Form from './form.vue'
	import portalMgrApi from '@/api/portal/portalMgrApi'

	const tableRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	const columns = [
		{
			title: '封面图片',
			dataIndex: 'coverUrl',
			align: 'center',
			width: 100
		},
		{
			title: '前台项目名称与描述',
			dataIndex: 'projectName',
			width: 280
		},
		{
			title: '团队组长',
			dataIndex: 'leaderName',
			width: 140
		},
		{
			title: '首页精选',
			dataIndex: 'isFeatured',
			width: 120,
			align: 'center'
		},
		{
			title: '置顶推荐',
			dataIndex: 'isPinned',
			width: 100,
			align: 'center'
		},
		{
			title: '推荐权重',
			dataIndex: 'sortCode',
			width: 100,
			align: 'center'
		},
		{
			title: '团队人数',
			dataIndex: 'currentMemberCount',
			width: 130
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right',
			width: 140
		}
	]

	const loadData = (parameter) => {
		return portalMgrApi.portalProjectPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
</script>

<style scoped>
	.thumb-box {
		width: 72px;
		height: 48px;
		border-radius: 6px;
		overflow: hidden;
		display: inline-block;
		border: 1px solid #e2e8f0;
	}
	.thumb-img {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}
	.text-ellipsis {
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}
</style>
