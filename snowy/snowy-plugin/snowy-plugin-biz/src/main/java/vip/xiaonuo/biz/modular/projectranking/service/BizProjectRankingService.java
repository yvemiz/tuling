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
package vip.xiaonuo.biz.modular.projectranking.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.projectranking.entity.BizProjectRanking;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingAddParam;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingEditParam;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingIdParam;
import vip.xiaonuo.biz.modular.projectranking.param.BizProjectRankingPageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 项目综合排行榜Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizProjectRankingService extends IService<BizProjectRanking> {

    /**
     * 获取项目综合排行榜分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizProjectRanking> page(BizProjectRankingPageParam bizProjectRankingPageParam);

    /**
     * 添加项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizProjectRankingAddParam bizProjectRankingAddParam);

    /**
     * 编辑项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizProjectRankingEditParam bizProjectRankingEditParam);

    /**
     * 删除项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizProjectRankingIdParam> bizProjectRankingIdParamList);

    /**
     * 获取项目综合排行榜详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizProjectRanking detail(BizProjectRankingIdParam bizProjectRankingIdParam);

    /**
     * 获取项目综合排行榜详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizProjectRanking queryEntity(Serializable id);

    /**
     * 下载项目综合排行榜导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出项目综合排行榜
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizProjectRankingIdParam> bizProjectRankingIdParamList, HttpServletResponse response) throws IOException;
}
