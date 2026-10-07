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
package vip.xiaonuo.biz.modular.teacherprofile.service.impl;

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
import vip.xiaonuo.biz.modular.teacherprofile.entity.BizTeacherProfile;
import vip.xiaonuo.biz.modular.teacherprofile.mapper.BizTeacherProfileMapper;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfileAddParam;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfileEditParam;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfileIdParam;
import vip.xiaonuo.biz.modular.teacherprofile.param.BizTeacherProfilePageParam;
import vip.xiaonuo.biz.modular.teacherprofile.service.BizTeacherProfileService;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import jakarta.annotation.Resource;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.StrUtil;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 教师档案管理Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizTeacherProfileServiceImpl extends ServiceImpl<BizTeacherProfileMapper, BizTeacherProfile> implements BizTeacherProfileService {

    @Resource
    private ClientUserService clientUserService;

    @Override
    public Page<BizTeacherProfile> page(BizTeacherProfilePageParam bizTeacherProfilePageParam) {
        QueryWrapper<BizTeacherProfile> queryWrapper = new QueryWrapper<BizTeacherProfile>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizTeacherProfilePageParam.getTeacherNo())) {
            queryWrapper.lambda().like(BizTeacherProfile::getTeacherNo, bizTeacherProfilePageParam.getTeacherNo());
        }
        if(ObjectUtil.isNotEmpty(bizTeacherProfilePageParam.getTitle())) {
            queryWrapper.lambda().eq(BizTeacherProfile::getTitle, bizTeacherProfilePageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(bizTeacherProfilePageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizTeacherProfile::getTeacherNo, bizTeacherProfilePageParam.getSearchKey());
        }

        if(ObjectUtil.isAllNotEmpty(bizTeacherProfilePageParam.getSortField(), bizTeacherProfilePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizTeacherProfilePageParam.getSortOrder());
            queryWrapper.orderBy(true, bizTeacherProfilePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizTeacherProfilePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizTeacherProfile::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizTeacherProfile::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizTeacherProfileAddParam bizTeacherProfileAddParam) {
        BizTeacherProfile bizTeacherProfile = BeanUtil.toBean(bizTeacherProfileAddParam, BizTeacherProfile.class);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加教师档案管理");
        }
        this.save(bizTeacherProfile);

        // 如果关联了C端用户，自动将该C端用户的 userRole 标记为 TEACHER
        if (StrUtil.isNotBlank(bizTeacherProfile.getUserId())) {
            ClientUser user = clientUserService.getById(bizTeacherProfile.getUserId());
            if (user != null) {
                JSONObject ext = StrUtil.isNotBlank(user.getExtJson()) && JSONUtil.isTypeJSONObject(user.getExtJson())
                        ? JSONUtil.parseObj(user.getExtJson()) : new JSONObject();
                ext.set("userRole", "TEACHER");
                user.setExtJson(ext.toString());
                clientUserService.updateById(user);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizTeacherProfileEditParam bizTeacherProfileEditParam) {
        BizTeacherProfile bizTeacherProfile = this.queryEntity(bizTeacherProfileEditParam.getId());
        BeanUtil.copyProperties(bizTeacherProfileEditParam, bizTeacherProfile);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑教师档案管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如：orgId
            // if(!loginUserDataScope.contains(bizTeacherProfile.getOrgId())) {
            //     throw new CommonException("您没有权限编辑该机构下的教师档案管理");
            // }
        }

        this.updateById(bizTeacherProfile);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizTeacherProfileIdParam> bizTeacherProfileIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除教师档案管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizTeacherProfileIdParamList, BizTeacherProfileIdParam::getId))
            //         .stream().map(BizTeacherProfile::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的教师档案管理");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizTeacherProfileIdParamList, BizTeacherProfileIdParam::getId));
    }

    @Override
    public BizTeacherProfile detail(BizTeacherProfileIdParam bizTeacherProfileIdParam) {
        return this.queryEntity(bizTeacherProfileIdParam.getId());
    }

    @Override
    public BizTeacherProfile queryEntity(Serializable id) {
        BizTeacherProfile bizTeacherProfile = this.getById(id);
        if(ObjectUtil.isEmpty(bizTeacherProfile)) {
            throw new CommonException("教师档案管理不存在，id值为：{}", id);
        }
        return bizTeacherProfile;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizTeacherProfileEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "教师档案管理导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizTeacherProfileEditParam.class).sheet("教师档案管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 教师档案管理导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "教师档案管理导入模板下载失败");
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
                    FileUtil.FILE_SEPARATOR + "bizTeacherProfileImportTemplate.xlsx"));
            // 读取excel
            List<BizTeacherProfileEditParam> bizTeacherProfileEditParamList =  EasyExcel.read(tempFile).head(BizTeacherProfileEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<BizTeacherProfile> allDataList = this.list();
            for (int i = 0; i < bizTeacherProfileEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, bizTeacherProfileEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", bizTeacherProfileEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 教师档案管理导入失败：", e);
            throw new CommonException("教师档案管理导入失败");
        }
    }

    public JSONObject doImport(List<BizTeacherProfile> allDataList, BizTeacherProfileEditParam bizTeacherProfileEditParam, int i) {
        String id = bizTeacherProfileEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, BizTeacherProfile::getId).indexOf(bizTeacherProfileEditParam.getId());
                BizTeacherProfile bizTeacherProfile;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    bizTeacherProfile = new BizTeacherProfile();
                } else {
                    bizTeacherProfile = allDataList.get(index);
                }
                BeanUtil.copyProperties(bizTeacherProfileEditParam, bizTeacherProfile);
                if(isAdd) {
                    allDataList.add(bizTeacherProfile);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, bizTeacherProfile);
                }
                this.saveOrUpdate(bizTeacherProfile);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<BizTeacherProfileIdParam> bizTeacherProfileIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizTeacherProfileEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizTeacherProfileIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizTeacherProfileIdParamList, BizTeacherProfileIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizTeacherProfileEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizTeacherProfileEditParam.class);
         }
         String fileName = "教师档案管理_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizTeacherProfileEditParam.class).sheet("教师档案管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 教师档案管理导出失败：", e);
         CommonResponseUtil.renderError(response, "教师档案管理导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
