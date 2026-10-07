<template>
	<div class="portal-site-config">
		<a-card :bordered="false" class="breadcrumb-card">
			<div style="display: flex; justify-content: space-between; align-items: center">
				<div>
					<h2 style="margin: 0; font-size: 20px; font-weight: 700; color: #1e293b">
						🖥️ 前台门户与视觉基础配置
					</h2>
					<p style="margin: 4px 0 0 0; color: #64748b; font-size: 13px">
						全面管控前台前端（Portal Web）的系统名称、品牌标语、首页 Banner 轮播图、页脚版权以及各页面专属文案。
					</p>
				</div>
				<a-button type="primary" @click="loadAllConfigs" :loading="loading">
					<template #icon><redo-outlined /></template>
					刷新配置
				</a-button>
			</div>
		</a-card>

		<a-card :bordered="false" style="margin-top: 16px">
			<a-tabs v-model:activeKey="activeTab" type="card">
				<!-- Tab 1: 站点基本信息 -->
				<a-tab-pane key="siteInfo" tab="🏢 站点基本品牌信息">
					<a-form :model="siteInfo" layout="vertical" style="max-width: 900px; margin-top: 12px">
						<a-row :gutter="24">
							<a-col :span="12">
								<a-form-item label="前台系统全称：" required>
									<a-input v-model:value="siteInfo.title" placeholder="如：大学生学科竞赛与算法实训平台" />
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-form-item label="前台副标题 / 品牌标语 (Slogan)：">
									<a-input v-model:value="siteInfo.subTitle" placeholder="如：释放创新潜能 · 协同组队攻坚 · 算法精准赋能" />
								</a-form-item>
							</a-col>
							<a-col :span="24">
								<a-form-item label="前台 Logo 图片链接 (留空显示默认图标)：">
									<a-input v-model:value="siteInfo.logo" placeholder="请输入 Logo 在线图片 URL" allow-clear />
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-form-item label="首页 Hero 核心大标语：">
									<a-input v-model:value="siteInfo.bannerTitle" placeholder="如：协同组队 · 算法冲榜 · AI精准赋能" />
								</a-form-item>
							</a-col>
							<a-col :span="12">
								<a-form-item label="ICP 备案号：">
									<a-input v-model:value="siteInfo.icp" placeholder="如：京ICP备20261001号-1" />
								</a-form-item>
							</a-col>
							<a-col :span="24">
								<a-form-item label="首页 Hero 核心描述文案：">
									<a-textarea v-model:value="siteInfo.bannerDesc" :rows="3" placeholder="首页主横幅介绍文案" />
								</a-form-item>
							</a-col>
							<a-col :span="24">
								<a-form-item label="页脚版权声明文案：">
									<a-input v-model:value="siteInfo.copyright" placeholder="如：Copyright © 2026 大学生学科竞赛与算法实训创新平台. All rights reserved." />
								</a-form-item>
							</a-col>
						</a-row>
						<a-form-item>
							<a-button type="primary" size="large" @click="saveSiteInfo" :loading="saving">
								💾 保存站点基本配置
							</a-button>
						</a-form-item>
					</a-form>
				</a-tab-pane>

				<!-- Tab 2: 首页轮播图 Banner 管理 -->
				<a-tab-pane key="banners" tab="🖼️ 首页 Banner 轮播图管理">
					<div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center">
						<span style="color: #64748b; font-size: 13px">配置前台首页顶部展示的轮播海报、推荐标题与跳转路由。</span>
						<a-space>
							<a-button type="primary" @click="openBannerModal()">
								<template #icon><plus-outlined /></template>
								添加轮播图
							</a-button>
							<a-button type="primary" ghost @click="saveBanners" :loading="saving">
								💾 立即生效轮播配置
							</a-button>
						</a-space>
					</div>

					<a-row :gutter="[16, 16]">
						<a-col :xs="24" :sm="12" :md="8" v-for="(b, idx) in banners" :key="b.id || idx">
							<a-card hoverable class="banner-preview-card">
								<template #cover>
									<div class="banner-img-box">
										<img :src="b.imgUrl" alt="Banner" class="banner-img" />
										<div class="banner-badge">#{{ idx + 1 }} 排序: {{ b.sortCode || 0 }}</div>
									</div>
								</template>
								<a-card-meta :title="b.title" :description="b.subTitle" />
								<div style="margin-top: 10px; font-size: 12px; color: #1677ff">
									🔗 跳转链接: <strong>{{ b.link || '无跳转' }}</strong>
								</div>
								<template #actions>
									<span @click="openBannerModal(b, idx)"><edit-outlined /> 编辑</span>
									<span @click="removeBanner(idx)" style="color: #ef4444"><delete-outlined /> 删除</span>
								</template>
							</a-card>
						</a-col>
					</a-row>
				</a-tab-pane>

				<!-- Tab 3: 各页面字段与文案配置 -->
				<a-tab-pane key="pageSettings" tab="📑 前台各页面字段文案">
					<a-form :model="pageSettings" layout="vertical" style="max-width: 900px; margin-top: 12px">
						<!-- 竞赛广场 -->
						<a-card title="🏆 竞赛广场页面文案" size="small" style="margin-bottom: 16px; background: #f8fafc">
							<a-row :gutter="16">
								<a-col :span="10">
									<a-form-item label="页面主标题：">
										<a-input v-model:value="pageSettings.competitionTitle" placeholder="如：🏆 学科竞赛广场" />
									</a-form-item>
								</a-col>
								<a-col :span="14">
									<a-form-item label="副标题/引导描述：">
										<a-input v-model:value="pageSettings.competitionDesc" placeholder="汇聚国家级、省级与校级顶级学科赛事..." />
									</a-form-item>
								</a-col>
							</a-row>
						</a-card>

						<!-- 团队大厅 -->
						<a-card title="👥 团队与招募大厅文案" size="small" style="margin-bottom: 16px; background: #f8fafc">
							<a-row :gutter="16">
								<a-col :span="10">
									<a-form-item label="页面主标题：">
										<a-input v-model:value="pageSettings.projectTitle" placeholder="如：👥 团队大厅与组长招募墙" />
									</a-form-item>
								</a-col>
								<a-col :span="14">
									<a-form-item label="副标题/引导描述：">
										<a-input v-model:value="pageSettings.projectDesc" placeholder="跨学科跨专业协同结组，投递组长招募需求..." />
									</a-form-item>
								</a-col>
							</a-row>
						</a-card>

						<!-- 算法集训 -->
						<a-card title="⚡ 算法集训中心文案" size="small" style="margin-bottom: 16px; background: #f8fafc">
							<a-row :gutter="16">
								<a-col :span="10">
									<a-form-item label="页面主标题：">
										<a-input v-model:value="pageSettings.algorithmTitle" placeholder="如：⚡ 算法集训与作业冲榜" />
									</a-form-item>
								</a-col>
								<a-col :span="14">
									<a-form-item label="副标题/引导描述：">
										<a-input v-model:value="pageSettings.algorithmDesc" placeholder="精选工业级与竞赛高频算法题目..." />
									</a-form-item>
								</a-col>
							</a-row>
						</a-card>

						<!-- 社区论坛 -->
						<a-card title="💬 社区论坛研讨文案" size="small" style="margin-bottom: 16px; background: #f8fafc">
							<a-row :gutter="16">
								<a-col :span="10">
									<a-form-item label="页面主标题：">
										<a-input v-model:value="pageSettings.forumTitle" placeholder="如：💬 竞赛社区与研讨论坛" />
									</a-form-item>
								</a-col>
								<a-col :span="14">
									<a-form-item label="副标题/引导描述：">
										<a-input v-model:value="pageSettings.forumDesc" placeholder="学术探讨、组长招募令、关注动态实时广播..." />
									</a-form-item>
								</a-col>
							</a-row>
						</a-card>

						<!-- 排行榜 -->
						<a-card title="🥇 综合排行榜页面文案" size="small" style="margin-bottom: 16px; background: #f8fafc">
							<a-row :gutter="16">
								<a-col :span="10">
									<a-form-item label="页面主标题：">
										<a-input v-model:value="pageSettings.rankingTitle" placeholder="如：🥇 卓越项目综合排行榜" />
									</a-form-item>
								</a-col>
								<a-col :span="14">
									<a-form-item label="副标题/引导描述：">
										<a-input v-model:value="pageSettings.rankingDesc" placeholder="综合团队算法完成度与竞赛活跃度..." />
									</a-form-item>
								</a-col>
							</a-row>
						</a-card>

						<a-form-item>
							<a-button type="primary" size="large" @click="savePageSettings" :loading="saving">
								💾 保存各页面文案字段
							</a-button>
						</a-form-item>
					</a-form>
				</a-tab-pane>
			</a-tabs>
		</a-card>

		<!-- 轮播图编辑弹窗 -->
		<a-modal
			v-model:open="bannerModalVisible"
			:title="editingBannerIndex > -1 ? '编辑轮播横幅' : '添加轮播横幅'"
			@ok="confirmSaveBanner"
			ok-text="确认添加/修改"
			cancel-text="取消"
		>
			<a-form :model="currentBanner" layout="vertical">
				<a-form-item label="海报主标题：" required>
					<a-input v-model:value="currentBanner.title" placeholder="请输入海报大标题" />
				</a-form-item>
				<a-form-item label="海报副标题：">
					<a-input v-model:value="currentBanner.subTitle" placeholder="请输入副标题说明" />
				</a-form-item>
				<a-form-item label="海报图片链接 (URL)：" required>
					<a-input v-model:value="currentBanner.imgUrl" placeholder="输入可访问的图片 URL" />
				</a-form-item>
				<div v-if="currentBanner.imgUrl" style="margin-bottom: 16px; text-align: center;">
					<img :src="currentBanner.imgUrl" alt="预览" style="max-height: 120px; border-radius: 6px; border: 1px solid #e2e8f0;" />
				</div>
				<a-form-item label="点击跳转路由 / 外部链接：">
					<a-input v-model:value="currentBanner.link" placeholder="如：/competition 或 /project 或 https://..." />
				</a-form-item>
				<a-form-item label="排序码 (数字越小越靠前)：">
					<a-input-number v-model:value="currentBanner.sortCode" style="width: 100%" :min="1" />
				</a-form-item>
			</a-form>
		</a-modal>
	</div>
</template>

<script setup name="portalSite">
	import { ref, onMounted } from 'vue'
	import { message } from 'ant-design-vue'
	import portalMgrApi from '@/api/portal/portalMgrApi'

	const activeTab = ref('siteInfo')
	const loading = ref(false)
	const saving = ref(false)

	const siteInfo = ref({
		title: '',
		subTitle: '',
		logo: '',
		bannerTitle: '',
		bannerDesc: '',
		copyright: '',
		icp: ''
	})

	const banners = ref([])
	const pageSettings = ref({
		competitionTitle: '',
		competitionDesc: '',
		projectTitle: '',
		projectDesc: '',
		algorithmTitle: '',
		algorithmDesc: '',
		forumTitle: '',
		forumDesc: '',
		rankingTitle: '',
		rankingDesc: ''
	})

	// 轮播图弹窗
	const bannerModalVisible = ref(false)
	const editingBannerIndex = ref(-1)
	const currentBanner = ref({})

	// 加载所有配置
	const loadAllConfigs = async () => {
		loading.value = true
		try {
			const res = await portalMgrApi.portalConfigGetAll()
			if (res) {
				if (res.PORTAL_SITE_INFO) {
					siteInfo.value = Object.assign({}, res.PORTAL_SITE_INFO)
				}
				if (res.PORTAL_BANNERS) {
					banners.value = Array.isArray(res.PORTAL_BANNERS) ? res.PORTAL_BANNERS : []
				}
				if (res.PORTAL_PAGE_SETTINGS) {
					pageSettings.value = Object.assign({}, res.PORTAL_PAGE_SETTINGS)
				}
			}
		} catch (e) {
			message.error('加载前台配置失败')
		} finally {
			loading.value = false
		}
	}

	// 保存站点基本信息
	const saveSiteInfo = async () => {
		saving.value = true
		try {
			await portalMgrApi.portalConfigSave({
				configKey: 'PORTAL_SITE_INFO',
				configName: '站点基础信息',
				configValue: siteInfo.value
			})
			message.success('站点基础信息已成功保存！')
		} catch (e) {
			message.error('保存失败')
		} finally {
			saving.value = false
		}
	}

	// 轮播图弹窗
	const openBannerModal = (item, idx) => {
		if (item) {
			editingBannerIndex.value = idx
			currentBanner.value = Object.assign({}, item)
		} else {
			editingBannerIndex.value = -1
			currentBanner.value = {
				id: Date.now().toString(),
				title: '',
				subTitle: '',
				imgUrl: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80',
				link: '/competition',
				sortCode: banners.value.length + 1
			}
		}
		bannerModalVisible.value = true
	}

	const confirmSaveBanner = () => {
		if (!currentBanner.value.title || !currentBanner.value.imgUrl) {
			message.warning('请填写海报标题与图片链接')
			return
		}
		if (editingBannerIndex.value > -1) {
			banners.value[editingBannerIndex.value] = Object.assign({}, currentBanner.value)
		} else {
			banners.value.push(Object.assign({}, currentBanner.value))
		}
		bannerModalVisible.value = false
		saveBanners()
	}

	const removeBanner = (idx) => {
		banners.value.splice(idx, 1)
		saveBanners()
	}

	// 保存轮播图
	const saveBanners = async () => {
		saving.value = true
		try {
			await portalMgrApi.portalConfigSave({
				configKey: 'PORTAL_BANNERS',
				configName: '首页Banner轮播图',
				configValue: banners.value
			})
			message.success('轮播图配置已生效并保存！')
		} catch (e) {
			message.error('轮播图保存失败')
		} finally {
			saving.value = false
		}
	}

	// 保存页面文案
	const savePageSettings = async () => {
		saving.value = true
		try {
			await portalMgrApi.portalConfigSave({
				configKey: 'PORTAL_PAGE_SETTINGS',
				configName: '各页面字段与文案配置',
				configValue: pageSettings.value
			})
			message.success('页面字段与文案配置保存成功！')
		} catch (e) {
			message.error('保存失败')
		} finally {
			saving.value = false
		}
	}

	onMounted(() => {
		loadAllConfigs()
	})
</script>

<style scoped>
	.banner-img-box {
		height: 140px;
		position: relative;
		overflow: hidden;
		background: #0f172a;
	}
	.banner-img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		transition: transform 0.3s ease;
	}
	.banner-img:hover {
		transform: scale(1.05);
	}
	.banner-badge {
		position: absolute;
		top: 8px;
		left: 8px;
		background: rgba(0, 0, 0, 0.6);
		color: #fff;
		padding: 2px 8px;
		border-radius: 4px;
		font-size: 11px;
	}
</style>
