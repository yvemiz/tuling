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
package vip.xiaonuo.biz.modular.algorithmurge.service.impl;

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
import vip.xiaonuo.biz.modular.algorithmurge.entity.BizAlgorithmUrge;
import vip.xiaonuo.biz.modular.algorithmurge.mapper.BizAlgorithmUrgeMapper;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeAddParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeEditParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgeIdParam;
import vip.xiaonuo.biz.modular.algorithmurge.param.BizAlgorithmUrgePageParam;
import vip.xiaonuo.biz.modular.algorithmurge.service.BizAlgorithmUrgeService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 算法催交记录Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizAlgorithmUrgeServiceImpl extends ServiceImpl<BizAlgorithmUrgeMapper, BizAlgorithmUrge> implements BizAlgorithmUrgeService {

    @Override
    public Page<BizAlgorithmUrge> page(BizAlgorithmUrgePageParam bizAlgorithmUrgePageParam) {
        QueryWrapper<BizAlgorithmUrge> queryWrapper = new QueryWrapper<BizAlgorithmUrge>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizAlgorithmUrgePageParam.getUrgeMessage())) {
            queryWrapper.lambda().like(BizAlgorithmUrge::getUrgeMessage, bizAlgorithmUrgePageParam.getUrgeMessage());
        }
        if(ObjectUtil.isNotEmpty(bizAlgorithmUrgePageParam.getTaskId())) {
            queryWrapper.lambda().eq(BizAlgorithmUrge::getTaskId, bizAlgorithmUrgePageParam.getTaskId());
        }
        if(ObjectUtil.isNotEmpty(bizAlgorithmUrgePageParam.getStudentId())) {
            queryWrapper.lambda().eq(BizAlgorithmUrge::getStudentId, bizAlgorithmUrgePageParam.getStudentId());
        }
        if(ObjectUtil.isNotEmpty(bizAlgorithmUrgePageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizAlgorithmUrge::getUrgeMessage, bizAlgorithmUrgePageParam.getSearchKey());
        }

        if(ObjectUtil.isAllNotEmpty(bizAlgorithmUrgePageParam.getSortField(), bizAlgorithmUrgePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizAlgorithmUrgePageParam.getSortOrder());
            queryWrapper.orderBy(true, bizAlgorithmUrgePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizAlgorithmUrgePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizAlgorithmUrge::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizAlgorithmUrge::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizAlgorithmUrgeAddParam bizAlgorithmUrgeAddParam) {
        BizAlgorithmUrge bizAlgorithmUrge = BeanUtil.toBean(bizAlgorithmUrgeAddParam, BizAlgorithmUrge.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加算法催交记录");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizAlgorithmUrge.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加算法催交记录");
            // }
        }

        this.save(bizAlgorithmUrge);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizAlgorithmUrgeEditParam bizAlgorithmUrgeEditParam) {
        BizAlgorithmUrge bizAlgorithmUrge = this.queryEntity(bizAlgorithmUrgeEditParam.getId());
        BeanUtil.copyProperties(bizAlgorithmUrgeEditParam, bizAlgorithmUrge);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑算法催交记录");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizAlgorithmUrge.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的算法催交记录");
            // }
        }

        this.updateById(bizAlgorithmUrge);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizAlgorithmUrgeIdParam> bizAlgorithmUrgeIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除算法催交记录");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizAlgorithmUrgeIdParamList, BizAlgorithmUrgeIdParam::getId))
            //         .stream().map(BizAlgorithmUrge::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的算法催交记录");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizAlgorithmUrgeIdParamList, BizAlgorithmUrgeIdParam::getId));
    }

    @Override
    public BizAlgorithmUrge detail(BizAlgorithmUrgeIdParam bizAlgorithmUrgeIdParam) {
        return this.queryEntity(bizAlgorithmUrgeIdParam.getId());
    }

    @Override
    public BizAlgorithmUrge queryEntity(Serializable id) {
        BizAlgorithmUrge bizAlgorithmUrge = this.getById(id);
        if(ObjectUtil.isEmpty(bizAlgorithmUrge)) {
            throw new CommonException("算法催交记录不存在，id值为：{}", id);
        }
        return bizAlgorithmUrge;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizAlgorithmUrgeEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "算法催交记录导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizAlgorithmUrgeEditParam.class).sheet("算法催交记录").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 算法催交记录导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "算法催交记录导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizAlgorithmUrgeImportTemplate.xlsx"));
            // 读取excel
            List<BizAlgorithmUrgeEditParam> bizAlgorithmUrgeEditParamList =  EasyExcel.read(tempFile).head(BizAlgorithmUrgeEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizAlgorithmUrge> allDataList = this.list();
            for (int i = 0; i < bizAlgorithmUrgeEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizAlgorithmUrgeEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizAlgorithmUrgeEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 算法催交记录导入失败：", e);
            throw new CommonException("算法催交记录导入失败");
        }
    }

    public JSONObject doImport(List<BizAlgorithmUrge> allDataList, BizAlgorithmUrgeEditParam bizAlgorithmUrgeEditParam, int i) {
        String id = bizAlgorithmUrgeEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizAlgorithmUrge::getId).indexOf(bizAlgorithmUrgeEditParam.getId());
                BizAlgorithmUrge bizAlgorithmUrge;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizAlgorithmUrge = new BizAlgorithmUrge();
                } else {
                    bizAlgorithmUrge = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizAlgorithmUrgeEditParam, bizAlgorithmUrge);
                if(isAdd) {
                    allDataList.add(bizAlgorithmUrge);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizAlgorithmUrge);
                }
                this.saveOrUpdate(bizAlgorithmUrge);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizAlgorithmUrgeIdParam> bizAlgorithmUrgeIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizAlgorithmUrgeEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizAlgorithmUrgeIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizAlgorithmUrgeIdParamList, BizAlgorithmUrgeIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizAlgorithmUrgeEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizAlgorithmUrgeEditParam.class);
         }
         String fileName = "算法催交记录_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizAlgorithmUrgeEditParam.class).sheet("算法催交记录").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 算法催交记录导出失败：", e);
         CommonResponseUtil.renderError(response, "算法催交记录导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
