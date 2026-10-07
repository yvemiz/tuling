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
package vip.xiaonuo.biz.modular.algorithmtask.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.algorithmtask.entity.BizAlgorithmTask;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskAddParam;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskEditParam;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskIdParam;
import vip.xiaonuo.biz.modular.algorithmtask.param.BizAlgorithmTaskPageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 算法题目任务Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizAlgorithmTaskService extends IService<BizAlgorithmTask> {

    /**
     * 获取算法题目任务分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizAlgorithmTask> page(BizAlgorithmTaskPageParam bizAlgorithmTaskPageParam);

    /**
     * 添加算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizAlgorithmTaskAddParam bizAlgorithmTaskAddParam);

    /**
     * 编辑算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizAlgorithmTaskEditParam bizAlgorithmTaskEditParam);

    /**
     * 删除算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizAlgorithmTaskIdParam> bizAlgorithmTaskIdParamList);

    /**
     * 获取算法题目任务详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizAlgorithmTask detail(BizAlgorithmTaskIdParam bizAlgorithmTaskIdParam);

    /**
     * 获取算法题目任务详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizAlgorithmTask queryEntity(Serializable id);

    /**
     * 下载算法题目任务导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出算法题目任务
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizAlgorithmTaskIdParam> bizAlgorithmTaskIdParamList, HttpServletResponse response) throws IOException;
}
