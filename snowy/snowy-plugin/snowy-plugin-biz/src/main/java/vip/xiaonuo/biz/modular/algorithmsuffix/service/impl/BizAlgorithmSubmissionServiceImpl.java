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
package vip.xiaonuo.biz.modular.algorithmsuffix.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import cn.dev33.satoken.stp.StpUtil;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.common.util.CommonServletUtil;
import vip.xiaonuo.common.util.CommonSqlUtil;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.biz.modular.algorithmsuffix.entity.BizAlgorithmSubmission;
import vip.xiaonuo.biz.modular.algorithmsuffix.mapper.BizAlgorithmSubmissionMapper;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionAddParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionEditParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionIdParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.param.BizAlgorithmSubmissionPageParam;
import vip.xiaonuo.biz.modular.algorithmsuffix.service.BizAlgorithmSubmissionService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 算法作业提交Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizAlgorithmSubmissionServiceImpl extends ServiceImpl<BizAlgorithmSubmissionMapper, BizAlgorithmSubmission> implements BizAlgorithmSubmissionService {

    @Override
    public Page<BizAlgorithmSubmission> page(BizAlgorithmSubmissionPageParam bizAlgorithmSubmissionPageParam) {
        QueryWrapper<BizAlgorithmSubmission> queryWrapper = new QueryWrapper<BizAlgorithmSubmission>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizAlgorithmSubmissionPageParam.getCodeLanguage())) {
            queryWrapper.lambda().eq(BizAlgorithmSubmission::getCodeLanguage, bizAlgorithmSubmissionPageParam.getCodeLanguage());
        }
        if(ObjectUtil.isNotEmpty(bizAlgorithmSubmissionPageParam.getStatus())) {
            queryWrapper.lambda().eq(BizAlgorithmSubmission::getStatus, bizAlgorithmSubmissionPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bizAlgorithmSubmissionPageParam.getTaskId())) {
            queryWrapper.lambda().eq(BizAlgorithmSubmission::getTaskId, bizAlgorithmSubmissionPageParam.getTaskId());
        }
        if(ObjectUtil.isNotEmpty(bizAlgorithmSubmissionPageParam.getStudentId())) {
            queryWrapper.lambda().eq(BizAlgorithmSubmission::getStudentId, bizAlgorithmSubmissionPageParam.getStudentId());
        }

        if(ObjectUtil.isAllNotEmpty(bizAlgorithmSubmissionPageParam.getSortField(), bizAlgorithmSubmissionPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizAlgorithmSubmissionPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizAlgorithmSubmissionPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizAlgorithmSubmissionPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizAlgorithmSubmission::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizAlgorithmSubmission::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizAlgorithmSubmissionAddParam bizAlgorithmSubmissionAddParam) {
        BizAlgorithmSubmission bizAlgorithmSubmission = BeanUtil.toBean(bizAlgorithmSubmissionAddParam, BizAlgorithmSubmission.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加算法作业提交");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizAlgorithmSubmission.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加算法作业提交");
            // }
        }

        this.save(bizAlgorithmSubmission);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizAlgorithmSubmissionEditParam bizAlgorithmSubmissionEditParam) {
        BizAlgorithmSubmission bizAlgorithmSubmission = this.queryEntity(bizAlgorithmSubmissionEditParam.getId());
        BeanUtil.copyProperties(bizAlgorithmSubmissionEditParam, bizAlgorithmSubmission);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑算法作业提交");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizAlgorithmSubmission.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的算法作业提交");
            // }
        }

        this.updateById(bizAlgorithmSubmission);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizAlgorithmSubmissionIdParam> bizAlgorithmSubmissionIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除算法作业提交");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizAlgorithmSubmissionIdParamList, BizAlgorithmSubmissionIdParam::getId))
            //         .stream().map(BizAlgorithmSubmission::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的算法作业提交");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizAlgorithmSubmissionIdParamList, BizAlgorithmSubmissionIdParam::getId));
    }

    @Override
    public BizAlgorithmSubmission detail(BizAlgorithmSubmissionIdParam bizAlgorithmSubmissionIdParam) {
        return this.queryEntity(bizAlgorithmSubmissionIdParam.getId());
    }

    @Override
    public BizAlgorithmSubmission queryEntity(Serializable id) {
        BizAlgorithmSubmission bizAlgorithmSubmission = this.getById(id);
        if(ObjectUtil.isEmpty(bizAlgorithmSubmission)) {
            throw new CommonException("算法作业提交不存在，id值为：{}", id);
        }
        return bizAlgorithmSubmission;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizAlgorithmSubmissionEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "算法作业提交导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizAlgorithmSubmissionEditParam.class).sheet("算法作业提交").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 算法作业提交导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "算法作业提交导入模板下载失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject importData(MultipartFile file) {
        try {
            int successCount = 0;
            int errorCount = 0;
            JSONArray errorDetail = JSONUtil.createArray();
            // 创建临时文件
            File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() +
                    FileUtil.FILE_SEPARATOR + "bizAlgorithmSubmissionImportTemplate.xlsx"));
            // 读取excel
            List<BizAlgorithmSubmissionEditParam> bizAlgorithmSubmissionEditParamList =  EasyExcel.read(tempFile).head(BizAlgorithmSubmissionEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizAlgorithmSubmission> allDataList = this.list();
            for (int i = 0; i < bizAlgorithmSubmissionEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizAlgorithmSubmissionEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizAlgorithmSubmissionEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 算法作业提交导入失败：", e);
            throw new CommonException("算法作业提交导入失败");
        }
    }

    public JSONObject doImport(List<BizAlgorithmSubmission> allDataList, BizAlgorithmSubmissionEditParam bizAlgorithmSubmissionEditParam, int i) {
        String id = bizAlgorithmSubmissionEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizAlgorithmSubmission::getId).indexOf(bizAlgorithmSubmissionEditParam.getId());
                BizAlgorithmSubmission bizAlgorithmSubmission;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizAlgorithmSubmission = new BizAlgorithmSubmission();
                } else {
                    bizAlgorithmSubmission = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizAlgorithmSubmissionEditParam, bizAlgorithmSubmission);
                if(isAdd) {
                    allDataList.add(bizAlgorithmSubmission);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizAlgorithmSubmission);
                }
                this.saveOrUpdate(bizAlgorithmSubmission);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizAlgorithmSubmissionIdParam> bizAlgorithmSubmissionIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizAlgorithmSubmissionEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizAlgorithmSubmissionIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizAlgorithmSubmissionIdParamList, BizAlgorithmSubmissionIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizAlgorithmSubmissionEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizAlgorithmSubmissionEditParam.class);
         }
         String fileName = "算法作业提交_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizAlgorithmSubmissionEditParam.class).sheet("算法作业提交").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 算法作业提交导出失败：", e);
         CommonResponseUtil.renderError(response, "算法作业提交导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
