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
package vip.xiaonuo.biz.modular.project.service.impl;

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
import vip.xiaonuo.biz.modular.project.entity.BizProject;
import vip.xiaonuo.biz.modular.project.mapper.BizProjectMapper;
import vip.xiaonuo.biz.modular.project.param.BizProjectAddParam;
import vip.xiaonuo.biz.modular.project.param.BizProjectEditParam;
import vip.xiaonuo.biz.modular.project.param.BizProjectIdParam;
import vip.xiaonuo.biz.modular.project.param.BizProjectPageParam;
import vip.xiaonuo.biz.modular.project.service.BizProjectService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 项目团队管理Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizProjectServiceImpl extends ServiceImpl<BizProjectMapper, BizProject> implements BizProjectService {

    @Override
    public Page<BizProject> page(BizProjectPageParam bizProjectPageParam) {
        QueryWrapper<BizProject> queryWrapper = new QueryWrapper<BizProject>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizProjectPageParam.getProjectName())) {
            queryWrapper.lambda().like(BizProject::getProjectName, bizProjectPageParam.getProjectName());
        }
        if(ObjectUtil.isNotEmpty(bizProjectPageParam.getIsPrivate())) {
            queryWrapper.lambda().eq(BizProject::getIsPrivate, bizProjectPageParam.getIsPrivate());
        }
        if(ObjectUtil.isNotEmpty(bizProjectPageParam.getStatus())) {
            queryWrapper.lambda().eq(BizProject::getStatus, bizProjectPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bizProjectPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizProject::getProjectName, bizProjectPageParam.getSearchKey());
        }

        if(ObjectUtil.isAllNotEmpty(bizProjectPageParam.getSortField(), bizProjectPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizProjectPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizProjectPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizProjectPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizProject::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizProject::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizProjectAddParam bizProjectAddParam) {
        BizProject bizProject = BeanUtil.toBean(bizProjectAddParam, BizProject.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加项目团队管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizProject.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加项目团队管理");
            // }
        }

        this.save(bizProject);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizProjectEditParam bizProjectEditParam) {
        BizProject bizProject = this.queryEntity(bizProjectEditParam.getId());
        BeanUtil.copyProperties(bizProjectEditParam, bizProject);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑项目团队管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizProject.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的项目团队管理");
            // }
        }

        this.updateById(bizProject);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizProjectIdParam> bizProjectIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除项目团队管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizProjectIdParamList, BizProjectIdParam::getId))
            //         .stream().map(BizProject::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的项目团队管理");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizProjectIdParamList, BizProjectIdParam::getId));
    }

    @Override
    public BizProject detail(BizProjectIdParam bizProjectIdParam) {
        return this.queryEntity(bizProjectIdParam.getId());
    }

    @Override
    public BizProject queryEntity(Serializable id) {
        BizProject bizProject = this.getById(id);
        if(ObjectUtil.isEmpty(bizProject)) {
            throw new CommonException("项目团队管理不存在，id值为：{}", id);
        }
        return bizProject;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizProjectEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "项目团队管理导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizProjectEditParam.class).sheet("项目团队管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 项目团队管理导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "项目团队管理导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizProjectImportTemplate.xlsx"));
            // 读取excel
            List<BizProjectEditParam> bizProjectEditParamList =  EasyExcel.read(tempFile).head(BizProjectEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizProject> allDataList = this.list();
            for (int i = 0; i < bizProjectEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizProjectEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizProjectEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 项目团队管理导入失败：", e);
            throw new CommonException("项目团队管理导入失败");
        }
    }

    public JSONObject doImport(List<BizProject> allDataList, BizProjectEditParam bizProjectEditParam, int i) {
        String id = bizProjectEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizProject::getId).indexOf(bizProjectEditParam.getId());
                BizProject bizProject;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizProject = new BizProject();
                } else {
                    bizProject = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizProjectEditParam, bizProject);
                if(isAdd) {
                    allDataList.add(bizProject);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizProject);
                }
                this.saveOrUpdate(bizProject);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizProjectIdParam> bizProjectIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizProjectEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizProjectIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizProjectIdParamList, BizProjectIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizProjectEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizProjectEditParam.class);
         }
         String fileName = "项目团队管理_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizProjectEditParam.class).sheet("项目团队管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 项目团队管理导出失败：", e);
         CommonResponseUtil.renderError(response, "项目团队管理导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
