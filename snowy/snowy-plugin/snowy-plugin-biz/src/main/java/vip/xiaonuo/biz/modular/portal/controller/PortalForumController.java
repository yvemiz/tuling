package vip.xiaonuo.biz.modular.portal.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.auth.core.annotation.SaClientCheckLogin;
import vip.xiaonuo.biz.modular.portal.param.PortalAuditParam;
import vip.xiaonuo.biz.modular.portal.param.PortalForumPostParam;
import vip.xiaonuo.biz.modular.portal.service.PortalService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 前台论坛社区与多级智能审核控制器
 *
 * @author antigravity
 * @date 2026/10/04
 */
@Tag(name = "前台门户-论坛社区控制器")
@RestController
@Validated
@RequestMapping("/client/portal/forum")
public class PortalForumController {

    @Resource
    private PortalService portalService;

    @Operation(summary = "分页获取已正式发布的论坛帖子列表")
    @GetMapping("/posts")
    public CommonResult<Page<JSONObject>> posts(
            @RequestParam(required = false, defaultValue = "ALL") String postType,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return portalService.pageForumPosts(postType, current, size);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/postDetail")
    public CommonResult<JSONObject> postDetail(@RequestParam String postId) {
        return portalService.getPostDetail(postId);
    }

    @Operation(summary = "所有注册用户均可发布论坛帖子（AI智能审查）")
    @SaClientCheckLogin
    @PostMapping("/createPost")
    public CommonResult<String> createPost(@RequestBody @Valid PortalForumPostParam param) {
        return portalService.createForumPost(param);
    }

    @Operation(summary = "教师与组长审核专区-待人工审核帖子列表")
    @SaClientCheckLogin
    @GetMapping("/pendingAuditPosts")
    public CommonResult<Page<JSONObject>> pendingAuditPosts(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return portalService.pagePendingAuditPosts(current, size);
    }

    @Operation(summary = "教师一票决裁-直接审核通过或驳回帖子")
    @SaClientCheckLogin
    @PostMapping("/teacherAudit")
    public CommonResult<String> teacherAudit(@RequestBody @Valid PortalAuditParam param) {
        return portalService.teacherAuditPost(param);
    }

    @Operation(summary = "组长联审投票-满2票组长赞成自动发布")
    @SaClientCheckLogin
    @PostMapping("/leaderAudit")
    public CommonResult<String> leaderAudit(@RequestBody @Valid PortalAuditParam param) {
        return portalService.leaderAuditPost(param);
    }

    @Operation(summary = "老师在论坛置顶/取消置顶内容")
    @SaClientCheckLogin
    @PostMapping("/togglePin")
    public CommonResult<String> togglePin(@RequestParam String postId, @RequestParam String isPinned) {
        return portalService.togglePinPost(postId, isPinned);
    }

    @Operation(summary = "发布评论或楼中楼回复")
    @SaClientCheckLogin
    @PostMapping("/addComment")
    public CommonResult<String> addComment(
            @RequestParam String postId,
            @RequestParam String content,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String replyToUserId) {
        return portalService.addComment(postId, content, parentId, replyToUserId);
    }

    @Operation(summary = "获取帖子的评论列表")
    @GetMapping("/comments")
    public CommonResult<List<JSONObject>> comments(@RequestParam String postId) {
        return portalService.getPostComments(postId);
    }
}
