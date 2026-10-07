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
package vip.xiaonuo.biz.modular.competition.service.impl;

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
import vip.xiaonuo.biz.modular.competition.entity.BizCompetition;
import vip.xiaonuo.biz.modular.competition.mapper.BizCompetitionMapper;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionAddParam;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionEditParam;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionIdParam;
import vip.xiaonuo.biz.modular.competition.param.BizCompetitionPageParam;
import vip.xiaonuo.biz.modular.competition.service.BizCompetitionService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 竞赛信息管理Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizCompetitionServiceImpl extends ServiceImpl<BizCompetitionMapper, BizCompetition> implements BizCompetitionService {

    @Override
    public Page<BizCompetition> page(BizCompetitionPageParam bizCompetitionPageParam) {
        QueryWrapper<BizCompetition> queryWrapper = new QueryWrapper<BizCompetition>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizCompetitionPageParam.getTitle())) {
            queryWrapper.lambda().like(BizCompetition::getTitle, bizCompetitionPageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(bizCompetitionPageParam.getCompetitionLevel())) {
            queryWrapper.lambda().eq(BizCompetition::getCompetitionLevel, bizCompetitionPageParam.getCompetitionLevel());
        }
        if(ObjectUtil.isNotEmpty(bizCompetitionPageParam.getStatus())) {
            queryWrapper.lambda().eq(BizCompetition::getStatus, bizCompetitionPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bizCompetitionPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizCompetition::getTitle, bizCompetitionPageParam.getSearchKey());
        }

        if(ObjectUtil.isAllNotEmpty(bizCompetitionPageParam.getSortField(), bizCompetitionPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizCompetitionPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizCompetitionPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizCompetitionPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizCompetition::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizCompetition::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizCompetitionAddParam bizCompetitionAddParam) {
        BizCompetition bizCompetition = BeanUtil.toBean(bizCompetitionAddParam, BizCompetition.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加竞赛信息管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizCompetition.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加竞赛信息管理");
            // }
        }

        this.save(bizCompetition);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizCompetitionEditParam bizCompetitionEditParam) {
        BizCompetition bizCompetition = this.queryEntity(bizCompetitionEditParam.getId());
        BeanUtil.copyProperties(bizCompetitionEditParam, bizCompetition);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑竞赛信息管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizCompetition.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的竞赛信息管理");
            // }
        }

        this.updateById(bizCompetition);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizCompetitionIdParam> bizCompetitionIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除竞赛信息管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizCompetitionIdParamList, BizCompetitionIdParam::getId))
            //         .stream().map(BizCompetition::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的竞赛信息管理");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizCompetitionIdParamList, BizCompetitionIdParam::getId));
    }

    @Override
    public BizCompetition detail(BizCompetitionIdParam bizCompetitionIdParam) {
        return this.queryEntity(bizCompetitionIdParam.getId());
    }

    @Override
    public BizCompetition queryEntity(Serializable id) {
        BizCompetition bizCompetition = this.getById(id);
        if(ObjectUtil.isEmpty(bizCompetition)) {
            throw new CommonException("竞赛信息管理不存在，id值为：{}", id);
        }
        return bizCompetition;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizCompetitionEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "竞赛信息管理导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizCompetitionEditParam.class).sheet("竞赛信息管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 竞赛信息管理导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "竞赛信息管理导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizCompetitionImportTemplate.xlsx"));
            // 读取excel
            List<BizCompetitionEditParam> bizCompetitionEditParamList =  EasyExcel.read(tempFile).head(BizCompetitionEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizCompetition> allDataList = this.list();
            for (int i = 0; i < bizCompetitionEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizCompetitionEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizCompetitionEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 竞赛信息管理导入失败：", e);
            throw new CommonException("竞赛信息管理导入失败");
        }
    }

    public JSONObject doImport(List<BizCompetition> allDataList, BizCompetitionEditParam bizCompetitionEditParam, int i) {
        String id = bizCompetitionEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizCompetition::getId).indexOf(bizCompetitionEditParam.getId());
                BizCompetition bizCompetition;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizCompetition = new BizCompetition();
                } else {
                    bizCompetition = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizCompetitionEditParam, bizCompetition);
                if(isAdd) {
                    allDataList.add(bizCompetition);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizCompetition);
                }
                this.saveOrUpdate(bizCompetition);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizCompetitionIdParam> bizCompetitionIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizCompetitionEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizCompetitionIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizCompetitionIdParamList, BizCompetitionIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizCompetitionEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizCompetitionEditParam.class);
         }
         String fileName = "竞赛信息管理_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizCompetitionEditParam.class).sheet("竞赛信息管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 竞赛信息管理导出失败：", e);
         CommonResponseUtil.renderError(response, "竞赛信息管理导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
