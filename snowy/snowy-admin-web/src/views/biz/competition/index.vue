<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState" style="margin-bottom: 16px">
			<a-row :gutter="16">
					<a-col :xs="24" :sm="12" :md="8" :lg="6">
						<a-form-item name="title" label="竞赛名称">
							<a-input v-model:value="searchFormState.title" placeholder="请输入竞赛名称" allow-clear />
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="12" :md="8" :lg="6">
						<a-form-item name="competitionLevel" label="竞赛级别">
							<a-select v-model:value="searchFormState.competitionLevel" placeholder="请选择竞赛级别" allow-clear>
								<a-select-option value="SCHOOL">校级 (SCHOOL)</a-select-option>
								<a-select-option value="PROVINCE">省级 (PROVINCE)</a-select-option>
								<a-select-option value="NATIONAL">国家级 (NATIONAL)</a-select-option>
								<a-select-option value="INTERNATIONAL">国际级 (INTERNATIONAL)</a-select-option>
							</a-select>
						</a-form-item>
					</a-col>
					<a-col :xs="24" :sm="12" :md="8" :lg="6">
						<a-form-item name="status" label="竞赛状态">
							<a-select v-model:value="searchFormState.status" placeholder="请选择竞赛状态" allow-clear>
								<a-select-option value="PENDING">未开始 (PENDING)</a-select-option>
								<a-select-option value="SIGNUP">报名中 (SIGNUP)</a-select-option>
								<a-select-option value="ONGOING">进行中 (ONGOING)</a-select-option>
								<a-select-option value="REVIEWING">评审中 (REVIEWING)</a-select-option>
								<a-select-option value="FINISHED">已结束 (FINISHED)</a-select-option>
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizCompetitionAdd')">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<a-button @click="importModelRef.onOpen()" v-if="hasPerm('bizCompetitionImport')">
						<template #icon><import-outlined /></template>
						<span>导入</span>
					</a-button>
					<a-button @click="exportData" v-if="hasPerm('bizCompetitionExport')">
						<template #icon><export-outlined /></template>
						<span>导出</span>
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizCompetitionBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:id-key="'id'"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchbizCompetition"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'competitionLevel'">
					<a-tag :color="getTagInfo('competitionLevel', record.competitionLevel).color">
						{{ getTagInfo('competitionLevel', record.competitionLevel).text }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-tag :color="getTagInfo('status', record.status).color">
						{{ getTagInfo('status', record.status).text }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'viewCount'">
					<span>{{ ((r) => `${r.viewCount || 0} / ${r.followCount || 0}`)(record) }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="detailRef.onOpen(record)" v-if="hasPerm('bizCompetitionDetail')">详情</a>
						<a-divider type="vertical" v-if="hasPerm(['bizCompetitionDetail', 'bizCompetitionEdit'], 'and')" />
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizCompetitionEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizCompetitionEdit', 'bizCompetitionDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deletebizCompetition(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizCompetitionDelete')">删除</a-button>
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

<script setup name="competition">
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import Detail from './detail.vue'
	import ImportModel from './importModel.vue'
	import downloadUtil from '@/utils/downloadUtil'
	import bizCompetitionApi from '@/api/biz/bizCompetitionApi'
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
			if ('competition' === 'competition') key = 'competitionStatus'
			else if ('competition' === 'project') key = 'projectStatus'
			else if ('competition' === 'projectrecruit') key = 'recruitStatus'
			else if ('competition' === 'projectflow') key = 'flowStatus'
			else if ('competition' === 'algorithmsuffix') key = 'submissionStatus'
		}
		if (DICT_TAGS[key] && DICT_TAGS[key][val]) {
			return DICT_TAGS[key][val]
		}
		return { text: val || '-', color: 'default' }
	}

	const columns = [
		{
			title: '竞赛名称',
			dataIndex: 'title'
		},
		{
			title: '竞赛级别',
			dataIndex: 'competitionLevel'
		},
		{
			title: '主办单位',
			dataIndex: 'organizer'
		},
		{
			title: '类别',
			dataIndex: 'category'
		},
		{
			title: '报名截止时间',
			dataIndex: 'signupEndTime'
		},
		{
			title: '竞赛状态',
			dataIndex: 'status'
		},
		{
			title: '队伍人数上限',
			dataIndex: 'maxMembers'
		},
		{
			title: '浏览/关注',
			dataIndex: 'viewCount'
		}
	]

	// 操作栏通过权限判断是否显示
	if (hasPerm(['bizCompetitionEdit', 'bizCompetitionDelete', 'bizCompetitionDetail'])) {
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
		return bizCompetitionApi.bizCompetitionPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deletebizCompetition = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		bizCompetitionApi.bizCompetitionDelete(params).then(() => {
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
			bizCompetitionApi.bizCompetitionExport(params).then((res) => {
				downloadUtil.resultDownload(res)
			})
		} else {
			bizCompetitionApi.bizCompetitionExport([]).then((res) => {
				downloadUtil.resultDownload(res)
			})
		}
	}
	// 批量删除
	const deleteBatchbizCompetition = (params) => {
		bizCompetitionApi.bizCompetitionDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
