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
package vip.xiaonuo.biz.modular.projectflow.service.impl;

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
import vip.xiaonuo.biz.modular.projectflow.entity.BizProjectFlow;
import vip.xiaonuo.biz.modular.projectflow.mapper.BizProjectFlowMapper;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowAddParam;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowEditParam;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowIdParam;
import vip.xiaonuo.biz.modular.projectflow.param.BizProjectFlowPageParam;
import vip.xiaonuo.biz.modular.projectflow.service.BizProjectFlowService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 组队申请流转Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizProjectFlowServiceImpl extends ServiceImpl<BizProjectFlowMapper, BizProjectFlow> implements BizProjectFlowService {

    @Override
    public Page<BizProjectFlow> page(BizProjectFlowPageParam bizProjectFlowPageParam) {
        QueryWrapper<BizProjectFlow> queryWrapper = new QueryWrapper<BizProjectFlow>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizProjectFlowPageParam.getDirection())) {
            queryWrapper.lambda().eq(BizProjectFlow::getDirection, bizProjectFlowPageParam.getDirection());
        }
        if(ObjectUtil.isNotEmpty(bizProjectFlowPageParam.getStatus())) {
            queryWrapper.lambda().eq(BizProjectFlow::getStatus, bizProjectFlowPageParam.getStatus());
        }

        if(ObjectUtil.isAllNotEmpty(bizProjectFlowPageParam.getSortField(), bizProjectFlowPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizProjectFlowPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizProjectFlowPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizProjectFlowPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizProjectFlow::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizProjectFlow::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizProjectFlowAddParam bizProjectFlowAddParam) {
        BizProjectFlow bizProjectFlow = BeanUtil.toBean(bizProjectFlowAddParam, BizProjectFlow.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加组队申请流转");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizProjectFlow.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加组队申请流转");
            // }
        }

        this.save(bizProjectFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizProjectFlowEditParam bizProjectFlowEditParam) {
        BizProjectFlow bizProjectFlow = this.queryEntity(bizProjectFlowEditParam.getId());
        BeanUtil.copyProperties(bizProjectFlowEditParam, bizProjectFlow);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑组队申请流转");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizProjectFlow.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的组队申请流转");
            // }
        }

        this.updateById(bizProjectFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizProjectFlowIdParam> bizProjectFlowIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除组队申请流转");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizProjectFlowIdParamList, BizProjectFlowIdParam::getId))
            //         .stream().map(BizProjectFlow::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的组队申请流转");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizProjectFlowIdParamList, BizProjectFlowIdParam::getId));
    }

    @Override
    public BizProjectFlow detail(BizProjectFlowIdParam bizProjectFlowIdParam) {
        return this.queryEntity(bizProjectFlowIdParam.getId());
    }

    @Override
    public BizProjectFlow queryEntity(Serializable id) {
        BizProjectFlow bizProjectFlow = this.getById(id);
        if(ObjectUtil.isEmpty(bizProjectFlow)) {
            throw new CommonException("组队申请流转不存在，id值为：{}", id);
        }
        return bizProjectFlow;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizProjectFlowEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "组队申请流转导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizProjectFlowEditParam.class).sheet("组队申请流转").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 组队申请流转导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "组队申请流转导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizProjectFlowImportTemplate.xlsx"));
            // 读取excel
            List<BizProjectFlowEditParam> bizProjectFlowEditParamList =  EasyExcel.read(tempFile).head(BizProjectFlowEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizProjectFlow> allDataList = this.list();
            for (int i = 0; i < bizProjectFlowEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizProjectFlowEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizProjectFlowEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 组队申请流转导入失败：", e);
            throw new CommonException("组队申请流转导入失败");
        }
    }

    public JSONObject doImport(List<BizProjectFlow> allDataList, BizProjectFlowEditParam bizProjectFlowEditParam, int i) {
        String id = bizProjectFlowEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizProjectFlow::getId).indexOf(bizProjectFlowEditParam.getId());
                BizProjectFlow bizProjectFlow;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizProjectFlow = new BizProjectFlow();
                } else {
                    bizProjectFlow = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizProjectFlowEditParam, bizProjectFlow);
                if(isAdd) {
                    allDataList.add(bizProjectFlow);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizProjectFlow);
                }
                this.saveOrUpdate(bizProjectFlow);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizProjectFlowIdParam> bizProjectFlowIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizProjectFlowEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizProjectFlowIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizProjectFlowIdParamList, BizProjectFlowIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizProjectFlowEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizProjectFlowEditParam.class);
         }
         String fileName = "组队申请流转_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizProjectFlowEditParam.class).sheet("组队申请流转").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 组队申请流转导出失败：", e);
         CommonResponseUtil.renderError(response, "组队申请流转导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
