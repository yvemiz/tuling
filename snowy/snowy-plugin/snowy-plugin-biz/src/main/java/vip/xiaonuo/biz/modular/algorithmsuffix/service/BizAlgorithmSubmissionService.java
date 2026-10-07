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
package vip.xiaonuo.biz.modular.algorithmsuffix.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.algorithmsuffix.entity.BizAlgorithmSubmission;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionAddParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionEditParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionIdParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionPageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 算法作业提交Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizAlgorithmSubmissionService extends IService<BizAlgorithmSubmission> {

    /**
     * 获取算法作业提交分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizAlgorithmSubmission> page(BizAlgorithmSubmissionPageParam bizAlgorithmSubmissionPageParam);

    /**
     * 添加算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizAlgorithmSubmissionAddParam bizAlgorithmSubmissionAddParam);

    /**
     * 编辑算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizAlgorithmSubmissionEditParam bizAlgorithmSubmissionEditParam);

    /**
     * 删除算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizAlgorithmSubmissionIdParam> bizAlgorithmSubmissionIdParamList);

    /**
     * 获取算法作业提交详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizAlgorithmSubmission detail(BizAlgorithmSubmissionIdParam bizAlgorithmSubmissionIdParam);

    /**
     * 获取算法作业提交详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizAlgorithmSubmission queryEntity(Serializable id);

    /**
     * 下载算法作业提交导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出算法作业提交
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizAlgorithmSubmissionIdParam> bizAlgorithmSubmissionIdParamList, HttpServletResponse response) throws IOException;
}
