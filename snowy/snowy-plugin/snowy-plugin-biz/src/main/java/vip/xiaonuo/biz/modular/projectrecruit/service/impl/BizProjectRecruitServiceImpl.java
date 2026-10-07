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
package vip.xiaonuo.biz.modular.projectrecruit.service.impl;

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
import vip.xiaonuo.biz.modular.projectrecruit.entity.BizProjectRecruit;
import vip.xiaonuo.biz.modular.projectrecruit.mapper.BizProjectRecruitMapper;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitAddParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitEditParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitIdParam;
import vip.xiaonuo.biz.modular.projectrecruit.param.BizProjectRecruitPageParam;
import vip.xiaonuo.biz.modular.projectrecruit.service.BizProjectRecruitService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 项目招募需求Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizProjectRecruitServiceImpl extends ServiceImpl<BizProjectRecruitMapper, BizProjectRecruit> implements BizProjectRecruitService {

    @Override
    public Page<BizProjectRecruit> page(BizProjectRecruitPageParam bizProjectRecruitPageParam) {
        QueryWrapper<BizProjectRecruit> queryWrapper = new QueryWrapper<BizProjectRecruit>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizProjectRecruitPageParam.getTitle())) {
            queryWrapper.lambda().like(BizProjectRecruit::getTitle, bizProjectRecruitPageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(bizProjectRecruitPageParam.getStatus())) {
            queryWrapper.lambda().eq(BizProjectRecruit::getStatus, bizProjectRecruitPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bizProjectRecruitPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizProjectRecruit::getTitle, bizProjectRecruitPageParam.getSearchKey());
        }

        if(ObjectUtil.isAllNotEmpty(bizProjectRecruitPageParam.getSortField(), bizProjectRecruitPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizProjectRecruitPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizProjectRecruitPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizProjectRecruitPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizProjectRecruit::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizProjectRecruit::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizProjectRecruitAddParam bizProjectRecruitAddParam) {
        BizProjectRecruit bizProjectRecruit = BeanUtil.toBean(bizProjectRecruitAddParam, BizProjectRecruit.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加项目招募需求");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizProjectRecruit.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加项目招募需求");
            // }
        }

        this.save(bizProjectRecruit);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizProjectRecruitEditParam bizProjectRecruitEditParam) {
        BizProjectRecruit bizProjectRecruit = this.queryEntity(bizProjectRecruitEditParam.getId());
        BeanUtil.copyProperties(bizProjectRecruitEditParam, bizProjectRecruit);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑项目招募需求");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizProjectRecruit.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的项目招募需求");
            // }
        }

        this.updateById(bizProjectRecruit);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizProjectRecruitIdParam> bizProjectRecruitIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除项目招募需求");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizProjectRecruitIdParamList, BizProjectRecruitIdParam::getId))
            //         .stream().map(BizProjectRecruit::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的项目招募需求");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizProjectRecruitIdParamList, BizProjectRecruitIdParam::getId));
    }

    @Override
    public BizProjectRecruit detail(BizProjectRecruitIdParam bizProjectRecruitIdParam) {
        return this.queryEntity(bizProjectRecruitIdParam.getId());
    }

    @Override
    public BizProjectRecruit queryEntity(Serializable id) {
        BizProjectRecruit bizProjectRecruit = this.getById(id);
        if(ObjectUtil.isEmpty(bizProjectRecruit)) {
            throw new CommonException("项目招募需求不存在，id值为：{}", id);
        }
        return bizProjectRecruit;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizProjectRecruitEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "项目招募需求导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizProjectRecruitEditParam.class).sheet("项目招募需求").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 项目招募需求导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "项目招募需求导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizProjectRecruitImportTemplate.xlsx"));
            // 读取excel
            List<BizProjectRecruitEditParam> bizProjectRecruitEditParamList =  EasyExcel.read(tempFile).head(BizProjectRecruitEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizProjectRecruit> allDataList = this.list();
            for (int i = 0; i < bizProjectRecruitEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizProjectRecruitEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizProjectRecruitEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 项目招募需求导入失败：", e);
            throw new CommonException("项目招募需求导入失败");
        }
    }

    public JSONObject doImport(List<BizProjectRecruit> allDataList, BizProjectRecruitEditParam bizProjectRecruitEditParam, int i) {
        String id = bizProjectRecruitEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizProjectRecruit::getId).indexOf(bizProjectRecruitEditParam.getId());
                BizProjectRecruit bizProjectRecruit;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizProjectRecruit = new BizProjectRecruit();
                } else {
                    bizProjectRecruit = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizProjectRecruitEditParam, bizProjectRecruit);
                if(isAdd) {
                    allDataList.add(bizProjectRecruit);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizProjectRecruit);
                }
                this.saveOrUpdate(bizProjectRecruit);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizProjectRecruitIdParam> bizProjectRecruitIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizProjectRecruitEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizProjectRecruitIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizProjectRecruitIdParamList, BizProjectRecruitIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizProjectRecruitEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizProjectRecruitEditParam.class);
         }
         String fileName = "项目招募需求_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizProjectRecruitEditParam.class).sheet("项目招募需求").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 项目招募需求导出失败：", e);
         CommonResponseUtil.renderError(response, "项目招募需求导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
