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
package vip.xiaonuo.biz.modular.memberscore.service.impl;

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
import vip.xiaonuo.biz.modular.memberscore.entity.BizMemberScore;
import vip.xiaonuo.biz.modular.memberscore.mapper.BizMemberScoreMapper;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScoreAddParam;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScoreEditParam;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScoreIdParam;
import vip.xiaonuo.biz.modular.memberscore.param.BizMemberScorePageParam;
import vip.xiaonuo.biz.modular.memberscore.service.BizMemberScoreService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 组员表现打分Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizMemberScoreServiceImpl extends ServiceImpl<BizMemberScoreMapper, BizMemberScore> implements BizMemberScoreService {

    @Override
    public Page<BizMemberScore> page(BizMemberScorePageParam bizMemberScorePageParam) {
        QueryWrapper<BizMemberScore> queryWrapper = new QueryWrapper<BizMemberScore>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizMemberScorePageParam.getEvaluatorRole())) {
            queryWrapper.lambda().eq(BizMemberScore::getEvaluatorRole, bizMemberScorePageParam.getEvaluatorRole());
        }

        if(ObjectUtil.isAllNotEmpty(bizMemberScorePageParam.getSortField(), bizMemberScorePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizMemberScorePageParam.getSortOrder());
            queryWrapper.orderBy(true, bizMemberScorePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizMemberScorePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizMemberScore::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizMemberScore::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizMemberScoreAddParam bizMemberScoreAddParam) {
        BizMemberScore bizMemberScore = BeanUtil.toBean(bizMemberScoreAddParam, BizMemberScore.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加组员表现打分");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizMemberScore.getOrgId())) {
            //     throw new CommonException("您没有权限在该机构下增加组员表现打分");
            // }
        }

        this.save(bizMemberScore);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizMemberScoreEditParam bizMemberScoreEditParam) {
        BizMemberScore bizMemberScore = this.queryEntity(bizMemberScoreEditParam.getId());
        BeanUtil.copyProperties(bizMemberScoreEditParam, bizMemberScore);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑组员表现打分");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizMemberScore.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的组员表现打分");
            // }
        }

        this.updateById(bizMemberScore);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizMemberScoreIdParam> bizMemberScoreIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除组员表现打分");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizMemberScoreIdParamList, BizMemberScoreIdParam::getId))
            //         .stream().map(BizMemberScore::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的组员表现打分");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizMemberScoreIdParamList, BizMemberScoreIdParam::getId));
    }

    @Override
    public BizMemberScore detail(BizMemberScoreIdParam bizMemberScoreIdParam) {
        return this.queryEntity(bizMemberScoreIdParam.getId());
    }

    @Override
    public BizMemberScore queryEntity(Serializable id) {
        BizMemberScore bizMemberScore = this.getById(id);
        if(ObjectUtil.isEmpty(bizMemberScore)) {
            throw new CommonException("组员表现打分不存在，id值为：{}", id);
        }
        return bizMemberScore;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizMemberScoreEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "组员表现打分导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizMemberScoreEditParam.class).sheet("组员表现打分").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 组员表现打分导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "组员表现打分导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizMemberScoreImportTemplate.xlsx"));
            // 读取excel
            List<BizMemberScoreEditParam> bizMemberScoreEditParamList =  EasyExcel.read(tempFile).head(BizMemberScoreEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizMemberScore> allDataList = this.list();
            for (int i = 0; i < bizMemberScoreEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizMemberScoreEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizMemberScoreEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 组员表现打分导入失败：", e);
            throw new CommonException("组员表现打分导入失败");
        }
    }

    public JSONObject doImport(List<BizMemberScore> allDataList, BizMemberScoreEditParam bizMemberScoreEditParam, int i) {
        String id = bizMemberScoreEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizMemberScore::getId).indexOf(bizMemberScoreEditParam.getId());
                BizMemberScore bizMemberScore;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizMemberScore = new BizMemberScore();
                } else {
                    bizMemberScore = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizMemberScoreEditParam, bizMemberScore);
                if(isAdd) {
                    allDataList.add(bizMemberScore);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizMemberScore);
                }
                this.saveOrUpdate(bizMemberScore);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizMemberScoreIdParam> bizMemberScoreIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizMemberScoreEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizMemberScoreIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizMemberScoreIdParamList, BizMemberScoreIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizMemberScoreEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizMemberScoreEditParam.class);
         }
         String fileName = "组员表现打分_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizMemberScoreEditParam.class).sheet("组员表现打分").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 组员表现打分导出失败：", e);
         CommonResponseUtil.renderError(response, "组员表现打分导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
