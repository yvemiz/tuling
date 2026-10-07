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
package vip.xiaonuo.biz.modular.competitionproject.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.competitionproject.entity.BizCompetitionProject;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectAddParam;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectEditParam;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectIdParam;
import vip.xiaonuo.biz.modular.competitionproject.param.BizCompetitionProjectPageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 竞赛报名关联Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizCompetitionProjectService extends IService<BizCompetitionProject> {

    /**
     * 获取竞赛报名关联分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizCompetitionProject> page(BizCompetitionProjectPageParam bizCompetitionProjectPageParam);

    /**
     * 添加竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizCompetitionProjectAddParam bizCompetitionProjectAddParam);

    /**
     * 编辑竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizCompetitionProjectEditParam bizCompetitionProjectEditParam);

    /**
     * 删除竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizCompetitionProjectIdParam> bizCompetitionProjectIdParamList);

    /**
     * 获取竞赛报名关联详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizCompetitionProject detail(BizCompetitionProjectIdParam bizCompetitionProjectIdParam);

    /**
     * 获取竞赛报名关联详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizCompetitionProject queryEntity(Serializable id);

    /**
     * 下载竞赛报名关联导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出竞赛报名关联
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizCompetitionProjectIdParam> bizCompetitionProjectIdParamList, HttpServletResponse response) throws IOException;
}
