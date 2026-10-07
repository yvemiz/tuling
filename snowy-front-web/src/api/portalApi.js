import request from '@/utils/request'

export default {
  // ========== 认证与个人中心 ==========
  login(data) {
    return request({ url: '/client/portal/auth/login', method: 'post', data })
  },
  register(data) {
    return request({ url: '/client/portal/auth/register', method: 'post', data })
  },
  demoLogin(role) {
    return request({ url: '/client/portal/auth/demoLogin', method: 'get', params: { role } })
  },
  logout() {
    return request({ url: '/client/portal/auth/logout', method: 'get' })
  },
  getMyProfile() {
    return request({ url: '/client/portal/user/myProfile', method: 'get' })
  },
  updateMyProfile(data) {
    return request({ url: '/client/portal/user/updateMyProfile', method: 'post', data })
  },
  getMyProjects() {
    return request({ url: '/client/portal/user/myProjects', method: 'get' })
  },
  getMyInvitations() {
    return request({ url: '/client/portal/user/myInvitations', method: 'get' })
  },
  handleInvitation(params) {
    return request({ url: '/client/portal/user/handleInvitation', method: 'post', params })
  },
  getAllStudents(keyword) {
    return request({ url: '/client/portal/user/allStudents', method: 'get', params: { keyword } })
  },
  getMyTodoList() {
    return request({ url: '/client/portal/user/todoList', method: 'get' })
  },

  // ========== 竞赛模块 ==========
  getCompetitions(params) {
    return request({ url: '/client/portal/competition/page', method: 'get', params })
  },
  getCompetitionDetail(competitionId) {
    return request({ url: '/client/portal/competition/detail', method: 'get', params: { competitionId } })
  },
  publishCompetition(data) {
    return request({ url: '/client/portal/competition/publish', method: 'post', data })
  },
  editCompetition(data) {
    return request({ url: '/client/portal/competition/edit', method: 'post', data })
  },
  deleteCompetition(id) {
    return request({ url: '/client/portal/competition/delete', method: 'post', params: { id } })
  },
  getCompetitionProjects(competitionId) {
    return request({ url: '/client/portal/competition/projects', method: 'get', params: { competitionId } })
  },
  createProject(data) {
    return request({ url: '/client/portal/competition/createProject', method: 'post', data })
  },
  individualSignup(competitionId) {
    return request({ url: '/client/portal/competition/individualSignup', method: 'post', params: { competitionId } })
  },
  toggleFollow(params) {
    return request({ url: '/client/portal/competition/toggleFollow', method: 'post', params })
  },
  getInterestedStudents(competitionId) {
    return request({ url: '/client/portal/competition/interestedStudents', method: 'get', params: { competitionId } })
  },
  inviteStudent(params) {
    return request({ url: '/client/portal/competition/inviteStudent', method: 'post', params })
  },
  publishRecruit(data) {
    return request({ url: '/client/portal/competition/publishRecruit', method: 'post', data })
  },
  listRecruits(keyword) {
    return request({ url: '/client/portal/competition/listRecruits', method: 'get', params: { keyword } })
  },
  uploadCertificate(data) {
    return request({ url: '/client/portal/competition/uploadCertificate', method: 'post', data })
  },
  getProjectCertificates(projectId) {
    return request({ url: '/client/portal/project/certificates', method: 'get', params: { projectId } })
  },
  getMyCertificates(competitionId) {
    return request({ url: '/client/portal/competition/myCertificates', method: 'get', params: { competitionId } })
  },
  verifyCertificate(params) {
    return request({ url: '/client/portal/competition/verifyCertificate', method: 'post', params })
  },

  // ========== 项目管理模块 ==========
  getProjectDetail(projectId) {
    return request({ url: '/client/portal/project/detail', method: 'get', params: { projectId } })
  },
  toggleProjectPrivacy(params) {
    return request({ url: '/client/portal/project/togglePrivacy', method: 'post', params })
  },
  removeMember(params) {
    return request({ url: '/client/portal/project/removeMember', method: 'post', data: params, params })
  },
  quitProject(params) {
    return request({ url: '/client/portal/project/quit', method: 'post', data: params, params })
  },
  disbandProject(params) {
    return request({ url: '/client/portal/project/disband', method: 'post', data: params, params })
  },
  updateMemberDuty(params) {
    return request({ url: '/client/portal/project/updateDuty', method: 'post', data: params, params })
  },
  applyToProject(params) {
    return request({ url: '/client/portal/project/apply', method: 'post', params })
  },
  getProjectApplicants(projectId) {
    return request({ url: '/client/portal/project/applicants', method: 'get', params: { projectId } })
  },
  handleProjectApplicant(params) {
    return request({ url: '/client/portal/project/handleApplicant', method: 'post', params })
  },
  bindProjectCompetition(params) {
    return request({ url: '/client/portal/project/bindCompetition', method: 'post', params })
  },
  confirmPromotion(params) {
    return request({ url: '/client/portal/project/confirmPromotion', method: 'post', params })
  },
  setProjectPromotion(params) {
    return request({ url: '/client/portal/competition/setProjectPromotion', method: 'post', params })
  },

  // ========== 算法训练模块 ==========
  publishAlgorithmTask(data) {
    return request({ url: '/client/portal/algorithm/publish', method: 'post', data })
  },
  getMyAlgorithmTasks() {
    return request({ url: '/client/portal/algorithm/myTasks', method: 'get' })
  },
  getTeamTaskProgress(params) {
    return request({ url: '/client/portal/algorithm/teamProgress', method: 'get', params })
  },
  urgeMember(params) {
    return request({ url: '/client/portal/algorithm/urge', method: 'post', params })
  },
  submitAlgorithm(data) {
    return request({ url: '/client/portal/algorithm/submit', method: 'post', data })
  },
  getMySubmissions(taskId) {
    return request({ url: '/client/portal/algorithm/mySubmissions', method: 'get', params: { taskId } })
  },
  gradeAlgorithmSubmission(params) {
    return request({ url: '/client/portal/algorithm/grade', method: 'post', params })
  },

  // 算法题库 CRUD 与分发发布
  getAlgorithmLibrary(params) {
    return request({ url: '/client/portal/algorithm/library/list', method: 'get', params })
  },
  saveAlgorithmProblem(data) {
    return request({ url: '/client/portal/algorithm/library/save', method: 'post', data })
  },
  updateAlgorithmProblem(data) {
    return request({ url: '/client/portal/algorithm/library/update', method: 'post', data })
  },
  deleteAlgorithmProblem(id) {
    return request({ url: '/client/portal/algorithm/library/delete', method: 'post', data: { id } })
  },
  dispatchAlgorithmTask(data) {
    return request({ url: '/client/portal/algorithm/library/dispatch', method: 'post', data })
  },

  // 教师各小组完成度概览与全量具体提交
  getTeacherGroupProgress() {
    return request({ url: '/client/portal/algorithm/teacher/groupProgress', method: 'get' })
  },
  getTeacherAllSubmissions(params) {
    return request({ url: '/client/portal/algorithm/teacher/allSubmissions', method: 'get', params })
  },

  // 组长多团队督训与一键催办
  getLeaderMyTeams() {
    return request({ url: '/client/portal/algorithm/leader/myTeams', method: 'get' })
  },
  getLeaderTeamOverview(params) {
    return request({ url: '/client/portal/algorithm/leader/teamOverview', method: 'get', params })
  },
  leaderBatchUrge(data) {
    return request({ url: '/client/portal/algorithm/leader/batchUrge', method: 'post', data })
  },

  // ========== 论坛社区模块 ==========
  getForumPosts(params) {
    return request({ url: '/client/portal/forum/posts', method: 'get', params })
  },
  getPostDetail(postId) {
    return request({ url: '/client/portal/forum/postDetail', method: 'get', params: { postId } })
  },
  createForumPost(data) {
    return request({ url: '/client/portal/forum/createPost', method: 'post', data })
  },
  getPendingAuditPosts(params) {
    return request({ url: '/client/portal/forum/pendingAuditPosts', method: 'get', params })
  },
  teacherAuditPost(data) {
    return request({ url: '/client/portal/forum/teacherAudit', method: 'post', data })
  },
  leaderAuditPost(data) {
    return request({ url: '/client/portal/forum/leaderAudit', method: 'post', data })
  },
  togglePinPost(params) {
    return request({ url: '/client/portal/forum/togglePin', method: 'post', params })
  },
  addComment(params) {
    return request({ url: '/client/portal/forum/addComment', method: 'post', params })
  },
  getPostComments(postId) {
    return request({ url: '/client/portal/forum/comments', method: 'get', params: { postId } })
  },

  // ========== AI机制与排行榜模块 ==========
  evaluateTeamWithAi(params) {
    return request({ url: '/client/portal/ai/evaluateTeam', method: 'post', params })
  },
  saveMemberScore(data) {
    return request({ url: '/client/portal/ai/saveMemberScore', method: 'post', data })
  },
  getMemberScores(projectId) {
    return request({ url: '/client/portal/ai/memberScores', method: 'get', params: { projectId } })
  },
  teacherEvaluateTeam(data) {
    return request({ url: '/client/portal/ai/teacherEvaluateTeam', method: 'post', data })
  },
  publishRanking(rankingId) {
    return request({ url: '/client/portal/ai/publishRanking', method: 'post', params: { rankingId } })
  },
  getRankings(params) {
    return request({ url: '/client/portal/ai/rankings', method: 'get', params })
  },

  // ========== 算法催促与文件上传 ==========
  getMyUrges() {
    return request({ url: '/client/portal/algorithm/myUrges', method: 'get' })
  },
  uploadFile(formData) {
    return request({
      url: '/client/portal/user/uploadFile',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // ========== 门户运营与动态配置 ==========
  getPortalConfigs() {
    return request({ url: '/portal/config/getAll', method: 'get' })
  },
  getForumBoards() {
    return request({ url: '/portal/board/list', method: 'get' })
  },

  // ========== 群聊与讨论互动模块 ==========
  sendChatMessage(data) {
    return request({ url: '/client/portal/chat/send', method: 'post', data })
  },
  getChatHistory(params) {
    return request({ url: '/client/portal/chat/history', method: 'get', params })
  },
  getChatUnreadCount(params) {
    return request({ url: '/client/portal/chat/unreadCount', method: 'get', params })
  },
  getUnreadCount(chatType, chatTargetId) {
    return request({ url: '/client/portal/chat/unreadCount', method: 'get', params: { chatType, chatTargetId } })
  },
  markChatRead(data) {
    return request({ url: '/client/portal/chat/markRead', method: 'post', data })
  },
  getChatGroupMembers(params) {
    return request({ url: '/client/portal/chat/groupMembers', method: 'get', params })
  },
  checkChatPermissions(params) {
    return request({ url: '/client/portal/chat/checkPermissions', method: 'get', params })
  }
}
