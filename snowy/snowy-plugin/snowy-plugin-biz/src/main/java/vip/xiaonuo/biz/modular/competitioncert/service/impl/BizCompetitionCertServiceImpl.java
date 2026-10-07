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
package vip.xiaonuo.biz.modular.competitioncert.service.impl;

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
import vip.xiaonuo.biz.modular.competitioncert.entity.BizCompetitionCert;
import vip.xiaonuo.biz.modular.competitioncert.mapper.BizCompetitionCertMapper;
import vip.xiaonuo.biz.modular.competitioncert.param.BizCompetitionCertAddParam;
import vip.xiaonuo.biz.modular.competitioncert.param.BizCompetitionCertEditParam;
import vip.xiaonuo.biz.modular.competitioncert.param.BizCompetitionCertIdParam;
import vip.xiaonuo.biz.modular.competitioncert.param.BizCompetitionCertPageParam;
import vip.xiaonuo.biz.modular.competitioncert.service.BizCompetitionCertService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 竞赛证书管理Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizCompetitionCertServiceImpl extends ServiceImpl<BizCompetitionCertMapper, BizCompetitionCert> implements BizCompetitionCertService {

    @Override
    public Page<BizCompetitionCert> page(BizCompetitionCertPageParam bizCompetitionCertPageParam) {
        QueryWrapper<BizCompetitionCert> queryWrapper = new QueryWrapper<BizCompetitionCert>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizCompetitionCertPageParam.getCertificateName())) {
            queryWrapper.lambda().like(BizCompetitionCert::getCertificateName, bizCompetitionCertPageParam.getCertificateName());
        }
        if(ObjectUtil.isNotEmpty(bizCompetitionCertPageParam.getVerifyStatus())) {
            queryWrapper.lambda().eq(BizCompetitionCert::getVerifyStatus, bizCompetitionCertPageParam.getVerifyStatus());
        }
        if(ObjectUtil.isNotEmpty(bizCompetitionCertPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizCompetitionCert::getCertificateName, bizCompetitionCertPageParam.getSearchKey());
        }

        if(ObjectUtil.isAllNotEmpty(bizCompetitionCertPageParam.getSortField(), bizCompetitionCertPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizCompetitionCertPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizCompetitionCertPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizCompetitionCertPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizCompetitionCert::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizCompetitionCert::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizCompetitionCertAddParam bizCompetitionCertAddParam) {
        BizCompetitionCert bizCompetitionCert = BeanUtil.toBean(bizCompetitionCertAddParam, BizCompetitionCert.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加竞赛证书管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizCompetitionCert.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加竞赛证书管理");
            // }
        }

        this.save(bizCompetitionCert);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizCompetitionCertEditParam bizCompetitionCertEditParam) {
        BizCompetitionCert bizCompetitionCert = this.queryEntity(bizCompetitionCertEditParam.getId());
        BeanUtil.copyProperties(bizCompetitionCertEditParam, bizCompetitionCert);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑竞赛证书管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizCompetitionCert.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的竞赛证书管理");
            // }
        }

        this.updateById(bizCompetitionCert);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizCompetitionCertIdParam> bizCompetitionCertIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除竞赛证书管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizCompetitionCertIdParamList, BizCompetitionCertIdParam::getId))
            //         .stream().map(BizCompetitionCert::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的竞赛证书管理");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizCompetitionCertIdParamList, BizCompetitionCertIdParam::getId));
    }

    @Override
    public BizCompetitionCert detail(BizCompetitionCertIdParam bizCompetitionCertIdParam) {
        return this.queryEntity(bizCompetitionCertIdParam.getId());
    }

    @Override
    public BizCompetitionCert queryEntity(Serializable id) {
        BizCompetitionCert bizCompetitionCert = this.getById(id);
        if(ObjectUtil.isEmpty(bizCompetitionCert)) {
            throw new CommonException("竞赛证书管理不存在，id值为：{}", id);
        }
        return bizCompetitionCert;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizCompetitionCertEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "竞赛证书管理导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizCompetitionCertEditParam.class).sheet("竞赛证书管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 竞赛证书管理导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "竞赛证书管理导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizCompetitionCertImportTemplate.xlsx"));
            // 读取excel
            List<BizCompetitionCertEditParam> bizCompetitionCertEditParamList =  EasyExcel.read(tempFile).head(BizCompetitionCertEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizCompetitionCert> allDataList = this.list();
            for (int i = 0; i < bizCompetitionCertEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizCompetitionCertEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizCompetitionCertEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 竞赛证书管理导入失败：", e);
            throw new CommonException("竞赛证书管理导入失败");
        }
    }

    public JSONObject doImport(List<BizCompetitionCert> allDataList, BizCompetitionCertEditParam bizCompetitionCertEditParam, int i) {
        String id = bizCompetitionCertEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizCompetitionCert::getId).indexOf(bizCompetitionCertEditParam.getId());
                BizCompetitionCert bizCompetitionCert;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizCompetitionCert = new BizCompetitionCert();
                } else {
                    bizCompetitionCert = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizCompetitionCertEditParam, bizCompetitionCert);
                if(isAdd) {
                    allDataList.add(bizCompetitionCert);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizCompetitionCert);
                }
                this.saveOrUpdate(bizCompetitionCert);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizCompetitionCertIdParam> bizCompetitionCertIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizCompetitionCertEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizCompetitionCertIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizCompetitionCertIdParamList, BizCompetitionCertIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizCompetitionCertEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizCompetitionCertEditParam.class);
         }
         String fileName = "竞赛证书管理_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizCompetitionCertEditParam.class).sheet("竞赛证书管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 竞赛证书管理导出失败：", e);
         CommonResponseUtil.renderError(response, "竞赛证书管理导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
