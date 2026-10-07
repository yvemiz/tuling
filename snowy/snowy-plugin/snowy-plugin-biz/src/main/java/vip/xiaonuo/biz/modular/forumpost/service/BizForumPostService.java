/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.biz.modular.forumpost.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.forumpost.entity.BizForumPost;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostAddParam;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostEditParam;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostIdParam;
import vip.xiaonuo.biz.modular.forumpost.param.BizForumPostPageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 论坛帖子管理Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizForumPostService extends IService<BizForumPost> {

    /**
     * 获取论坛帖子管理分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizForumPost> page(BizForumPostPageParam bizForumPostPageParam);

    /**
     * 添加论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizForumPostAddParam bizForumPostAddParam);

    /**
     * 编辑论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizForumPostEditParam bizForumPostEditParam);

    /**
     * 删除论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizForumPostIdParam> bizForumPostIdParamList);

    /**
     * 获取论坛帖子管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizForumPost detail(BizForumPostIdParam bizForumPostIdParam);

    /**
     * 获取论坛帖子管理详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizForumPost queryEntity(Serializable id);

    /**
     * 下载论坛帖子管理导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出论坛帖子管理
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizForumPostIdParam> bizForumPostIdParamList, HttpServletResponse response) throws IOException;
}
