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
package vip.xiaonuo.biz.modular.projectrecruit.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.projectrecruit.entity.BizProjectRecruit;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitAddParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitEditParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitIdParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitPageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 项目招募需求Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizProjectRecruitService extends IService<BizProjectRecruit> {

    /**
     * 获取项目招募需求分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizProjectRecruit> page(BizProjectRecruitPageParam bizProjectRecruitPageParam);

    /**
     * 添加项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizProjectRecruitAddParam bizProjectRecruitAddParam);

    /**
     * 编辑项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizProjectRecruitEditParam bizProjectRecruitEditParam);

    /**
     * 删除项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizProjectRecruitIdParam> bizProjectRecruitIdParamList);

    /**
     * 获取项目招募需求详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizProjectRecruit detail(BizProjectRecruitIdParam bizProjectRecruitIdParam);

    /**
     * 获取项目招募需求详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizProjectRecruit queryEntity(Serializable id);

    /**
     * 下载项目招募需求导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出项目招募需求
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizProjectRecruitIdParam> bizProjectRecruitIdParamList, HttpServletResponse response) throws IOException;
}
