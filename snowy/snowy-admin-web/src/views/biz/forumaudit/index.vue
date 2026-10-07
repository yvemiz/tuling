<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState" style="margin-bottom: 16px">
			<a-row :gutter="16">
					<a-col :xs="24" :sm="12" :md="8" :lg="6">
						<a-form-item name="auditorType" label="审核主体">
							<a-select v-model:value="searchFormState.auditorType" placeholder="请选择审核主体" allow-clear>
								<a-select-option value="AI">AI审查 (AI)</a-select-option>
								<a-select-option value="TEACHER">教师终审 (TEACHER)</a-select-option>
								<a-select-option value="LEADER">组长联审 (LEADER)</a-select-option>
							</a-select>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="12" :md="8" :lg="6">
						<a-form-item name="auditResult" label="审核决议">
							<a-select v-model:value="searchFormState.auditResult" placeholder="请选择审核决议" allow-clear>
								<a-select-option value="PASS">通过 (PASS)</a-select-option>
								<a-select-option value="REJECT">驳回 (REJECT)</a-select-option>
							</a-select>
						</a-form-item>
					</a-col>
				<a-col :xs="24" :sm="12" :md="8" :lg="6">
					<a-form-item>
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
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
			:scroll="{ x: 'max-content' }"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizForumAuditAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('bizForumAuditImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('bizForumAuditExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizForumAuditBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:id-key="'id'"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchbizForumAudit"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'auditorType'">
					<a-tag :color="getTagInfo('auditorType', record.auditorType).color">
						{{ getTagInfo('auditorType', record.auditorType).text }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'auditResult'">
					<a-tag :color="getTagInfo('auditResult', record.auditResult).color">
						{{ getTagInfo('auditResult', record.auditResult).text }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="detailRef.onOpen(record)" v-if="hasPerm('bizForumAuditDetail')">详情</a>
						<a-divider type="vertical" v-if="hasPerm(['bizForumAuditDetail', 'bizForumAuditEdit'], 'and')" />
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizForumAuditEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizForumAuditEdit', 'bizForumAuditDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deletebizForumAudit(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizForumAuditDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>
	<ImportModel ref="importModelRef" />
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<Detail ref="detailRef" />
</template>

<script setup name="forumaudit">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import Detail from './detail.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import bizForumAuditApi from '@/api/biz/bizForumAuditApi'
	const tableRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	const importModelRef = ref()
	const formRef = ref()
	const detailRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	
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

	const DICT_TAGS = {
	"competitionLevel": {
		"SCHOOL": {
			"text": "校级",
			"color": "blue"
		},
		"PROVINCE": {
			"text": "省级",
			"color": "purple"
		},
		"NATIONAL": {
			"text": "国家级",
			"color": "orange"
		},
		"INTERNATIONAL": {
			"text": "国际级",
			"color": "red"
		}
	},
	"competitionStatus": {
		"PENDING": {
			"text": "未开始",
			"color": "default"
		},
		"SIGNUP": {
			"text": "报名中",
			"color": "processing"
		},
		"ONGOING": {
			"text": "进行中",
			"color": "success"
		},
		"REVIEWING": {
			"text": "评审中",
			"color": "warning"
		},
		"FINISHED": {
			"text": "已结束",
			"color": "default"
		}
	},
	"projectStatus": {
		"RECRUITING": {
			"text": "招募中",
			"color": "blue"
		},
		"FORMULATED": {
			"text": "组队完成",
			"color": "green"
		},
		"ARCHIVED": {
			"text": "已归档",
			"color": "default"
		}
	},
	"recruitStatus": {
		"OPEN": {
			"text": "招募中",
			"color": "blue"
		},
		"CLOSED": {
			"text": "已关闭",
			"color": "default"
		}
	},
	"flowStatus": {
		"PENDING": {
			"text": "待处理",
			"color": "orange"
		},
		"ACCEPTED": {
			"text": "已入组",
			"color": "green"
		},
		"REJECTED": {
			"text": "已拒绝",
			"color": "red"
		}
	},
	"submissionStatus": {
		"SUBMITTED": {
			"text": "已提交",
			"color": "blue"
		},
		"GRADED": {
			"text": "已评分",
			"color": "green"
		},
		"REWORK": {
			"text": "需重做",
			"color": "red"
		}
	},
	"isPrivate": {
		"YES": {
			"text": "私密团队",
			"color": "error"
		},
		"NO": {
			"text": "公开团队",
			"color": "success"
		}
	},
	"isPinned": {
		"YES": {
			"text": "置顶",
			"color": "red"
		},
		"NO": {
			"text": "普通",
			"color": "default"
		}
	},
	"isPublished": {
		"YES": {
			"text": "已上榜",
			"color": "green"
		},
		"NO": {
			"text": "草稿",
			"color": "orange"
		}
	},
	"difficulty": {
		"EASY": {
			"text": "简单",
			"color": "green"
		},
		"MEDIUM": {
			"text": "中等",
			"color": "orange"
		},
		"HARD": {
			"text": "困难",
			"color": "red"
		}
	},
	"auditStatus": {
		"PENDING": {
			"text": "待审核",
			"color": "orange"
		},
		"PASSED": {
			"text": "已通过",
			"color": "green"
		},
		"REJECTED": {
			"text": "已驳回",
			"color": "red"
		},
		"AI_PENDING": {
			"text": "AI审核中",
			"color": "cyan"
		},
		"PUBLISHED": {
			"text": "已发布",
			"color": "green"
		},
		"MANUAL_PENDING": {
			"text": "转人工",
			"color": "orange"
		}
	},
	"verifyStatus": {
		"PENDING": {
			"text": "待认证",
			"color": "orange"
		},
		"VERIFIED": {
			"text": "已认证",
			"color": "green"
		},
		"REJECTED": {
			"text": "已驳回",
			"color": "red"
		}
	},
	"signupType": {
		"PROJECT": {
			"text": "团队参赛",
			"color": "blue"
		},
		"INDIVIDUAL": {
			"text": "个人参赛",
			"color": "cyan"
		}
	},
	"memberRole": {
		"LEADER": {
			"text": "队长/组长",
			"color": "gold"
		},
		"MEMBER": {
			"text": "组员",
			"color": "blue"
		}
	},
	"memberStatus": {
		"ACTIVE": {
			"text": "正式在组",
			"color": "green"
		},
		"QUIT": {
			"text": "已退出",
			"color": "default"
		}
	},
	"targetType": {
		"COMPETITION": {
			"text": "学科竞赛",
			"color": "blue"
		},
		"PROJECT": {
			"text": "项目团队",
			"color": "green"
		}
	},
	"direction": {
		"INVITE": {
			"text": "组长邀请",
			"color": "purple"
		},
		"APPLY": {
			"text": "学生申请",
			"color": "cyan"
		}
	},
	"postType": {
		"GENERAL": {
			"text": "交流讨论",
			"color": "blue"
		},
		"RECRUITMENT": {
			"text": "组队招聘",
			"color": "green"
		},
		"FOLLOW_EVENT": {
			"text": "关注动态",
			"color": "cyan"
		},
		"COMPETITION_EVENT": {
			"text": "竞赛发布",
			"color": "purple"
		}
	},
	"auditorType": {
		"AI": {
			"text": "智能AI",
			"color": "blue"
		},
		"TEACHER": {
			"text": "教师终审",
			"color": "purple"
		},
		"LEADER": {
			"text": "组长联审",
			"color": "gold"
		}
	},
	"auditResult": {
		"PASS": {
			"text": "通过",
			"color": "green"
		},
		"REJECT": {
			"text": "驳回",
			"color": "red"
		}
	},
	"evaluatorRole": {
		"LEADER": {
			"text": "组长打分",
			"color": "blue"
		},
		"TEACHER": {
			"text": "教师评分",
			"color": "purple"
		}
	},
	"teacherTitle": {
		"教授": {
			"text": "教授",
			"color": "red"
		},
		"副教授": {
			"text": "副教授",
			"color": "purple"
		},
		"讲师": {
			"text": "讲师",
			"color": "blue"
		},
		"助教": {
			"text": "助教",
			"color": "cyan"
		}
	}
}

	const getTagInfo = (col, val) => {
		let key = col
		if (col === 'title') key = 'teacherTitle'
		if (col === 'status') {
			if ('forumaudit' === 'competition') key = 'competitionStatus'
			else if ('forumaudit' === 'project') key = 'projectStatus'
			else if ('forumaudit' === 'projectrecruit') key = 'recruitStatus'
			else if ('forumaudit' === 'projectflow') key = 'flowStatus'
			else if ('forumaudit' === 'algorithmsuffix') key = 'submissionStatus'
		}
		if (DICT_TAGS[key] && DICT_TAGS[key][val]) {
			return DICT_TAGS[key][val]
		}
		return { text: val || '-', color: 'default' }
	}

	const columns = [
		{
			title: '审核帖子ID',
			dataIndex: 'postId'
		},
		{
			title: '审核主体',
			dataIndex: 'auditorType'
		},
		{
			title: '审核决议',
			dataIndex: 'auditResult'
		},
		{
			title: '审核意见',
			dataIndex: 'auditReason'
		},
		{
			title: '审核时间',
			dataIndex: 'auditTime'
		}
	]

	// 操作栏通过权限判断是否显示
	if (hasPerm(['bizForumAuditEdit', 'bizForumAuditDelete', 'bizForumAuditDetail'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right',
			width: 160
		})
	}
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: true,
			clear: () => {
				selectedRowKeys.value = []
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const loadData = (parameter) => {
		return bizForumAuditApi.bizForumAuditPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deletebizForumAudit = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		bizForumAuditApi.bizForumAuditDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 导出
	const exportData = () => {
		if (selectedRowKeys.value.length > 0) {
			const params = selectedRowKeys.value.map((m) => {
				return {
					id: m
				}
			})
			bizForumAuditApi.bizForumAuditExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			bizForumAuditApi.bizForumAuditExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchbizForumAudit = (params) => {
		bizForumAuditApi.bizForumAuditDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
