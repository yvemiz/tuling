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
package vip.xiaonuo.biz.modular.algorithmurge.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.algorithmurge.entity.BizAlgorithmUrge;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeAddParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeEditParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeIdParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgePageParam;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 算法催交记录Service接口
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
public interface BizAlgorithmUrgeService extends IService<BizAlgorithmUrge> {

    /**
     * 获取算法催交记录分页
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    Page<BizAlgorithmUrge> page(BizAlgorithmUrgePageParam bizAlgorithmUrgePageParam);

    /**
     * 添加算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void add(BizAlgorithmUrgeAddParam bizAlgorithmUrgeAddParam);

    /**
     * 编辑算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void edit(BizAlgorithmUrgeEditParam bizAlgorithmUrgeEditParam);

    /**
     * 删除算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void delete(List<BizAlgorithmUrgeIdParam> bizAlgorithmUrgeIdParamList);

    /**
     * 获取算法催交记录详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    BizAlgorithmUrge detail(BizAlgorithmUrgeIdParam bizAlgorithmUrgeIdParam);

    /**
     * 获取算法催交记录详情
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    BizAlgorithmUrge queryEntity(Serializable id);

    /**
     * 下载算法催交记录导入模板
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出算法催交记录
     *
     * @author antigravity
     * @date  2026/10/04 13:31
     */
    void exportData(List<BizAlgorithmUrgeIdParam> bizAlgorithmUrgeIdParamList, HttpServletResponse response) throws IOException;
}
