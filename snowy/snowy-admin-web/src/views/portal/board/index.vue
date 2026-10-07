<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState" style="margin-bottom: 16px">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="12" :md="8" :lg="6">
					<a-form-item name="searchKey" label="板块关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="输入板块名称/编码/描述" allow-clear />
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
			<template #operator>
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()">
						<template #icon><plus-outlined /></template>
						新增社区板块
					</a-button>
				</a-space>
			</template>

			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'icon'">
					<span style="font-size: 20px">{{ record.icon || '💬' }}</span>
				</template>
				<template v-if="column.dataIndex === 'boardName'">
					<a-tag :color="record.color || 'blue'" style="font-size: 13px; padding: 2px 8px">
						{{ record.icon || '' }} {{ record.boardName }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'boardCode'">
					<code>{{ record.boardCode }}</code>
				</template>
				<template v-if="column.dataIndex === 'postPermission'">
					<a-tag v-if="record.postPermission === 'TEACHER'" color="purple">仅教师</a-tag>
					<a-tag v-else-if="record.postPermission === 'LEADER'" color="orange">组长与教师</a-tag>
					<a-tag v-else color="green">全员可发</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:checked="record.status === 'ENABLE'"
						checked-children="启用"
						un-checked-children="停用"
						@change="(checked) => handleToggleStatus(record, checked)"
					/>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此社区板块吗？" @confirm="handleDelete(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>

	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="portalForumBoard">
	import { ref } from 'vue'
	import { message } from 'ant-design-vue'
	import Form from './form.vue'
	import portalMgrApi from '@/api/portal/portalMgrApi'

	const tableRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	const columns = [
		{
			title: '图标',
			dataIndex: 'icon',
			align: 'center',
			width: 70
		},
		{
			title: '板块名称',
			dataIndex: 'boardName',
			width: 170
		},
		{
			title: '板块唯一编码',
			dataIndex: 'boardCode',
			width: 170
		},
		{
			title: '发帖权限',
			dataIndex: 'postPermission',
			width: 130
		},
		{
			title: '排序码',
			dataIndex: 'sortCode',
			width: 90
		},
		{
			title: '板块简介',
			dataIndex: 'description',
			ellipsis: true
		},
		{
			title: '状态',
			dataIndex: 'status',
			width: 110,
			align: 'center'
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
		return portalMgrApi.portalBoardPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	const handleToggleStatus = (record, checked) => {
		const newStatus = checked ? 'ENABLE' : 'DISABLE'
		portalMgrApi.portalBoardChangeStatus({ id: record.id, status: newStatus }).then(() => {
			message.success(`已${checked ? '启用' : '停用'}板块【${record.boardName}】`)
			tableRef.value.refresh()
		})
	}

	const handleDelete = (record) => {
		portalMgrApi.portalBoardDelete([record.id]).then(() => {
			message.success('板块删除成功')
			tableRef.value.refresh()
		})
	}
</script>
