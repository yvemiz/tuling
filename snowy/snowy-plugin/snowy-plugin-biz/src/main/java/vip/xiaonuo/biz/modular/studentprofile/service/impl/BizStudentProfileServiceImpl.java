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
package vip.xiaonuo.biz.modular.studentprofile.service.impl;

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
import vip.xiaonuo.biz.modular.studentprofile.entity.BizStudentProfile;
import vip.xiaonuo.biz.modular.studentprofile.mapper.BizStudentProfileMapper;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileAddParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileEditParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileIdParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfilePageParam;
import vip.xiaonuo.biz.modular.studentprofile.param.BizStudentProfileImportParam;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import vip.xiaonuo.biz.modular.studentprofile.service.BizStudentProfileService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * 学生档案管理Service接口实现类
 *
 * @author antigravity
 * @date  2026/10/04 13:31
 **/
@Service
public class BizStudentProfileServiceImpl extends ServiceImpl<BizStudentProfileMapper, BizStudentProfile> implements BizStudentProfileService {

    @Resource
    private ClientUserService clientUserService;

    @Override
    public Page<BizStudentProfile> page(BizStudentProfilePageParam bizStudentProfilePageParam) {
        QueryWrapper<BizStudentProfile> queryWrapper = new QueryWrapper<BizStudentProfile>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getStudentNo())) {
            queryWrapper.lambda().like(BizStudentProfile::getStudentNo, bizStudentProfilePageParam.getStudentNo());
        }
        if(ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getMajor())) {
            queryWrapper.lambda().like(BizStudentProfile::getMajor, bizStudentProfilePageParam.getMajor());
        }
        if(ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getGrade())) {
            queryWrapper.lambda().eq(BizStudentProfile::getGrade, bizStudentProfilePageParam.getGrade());
        }

        // 支持通过学生姓名、账号、手机号或者searchKey综合搜索
        Set<String> matchedUserIds = null;
        boolean hasUserCondition = ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getName()) ||
                ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getAccount()) ||
                ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getPhone()) ||
                ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getSearchKey());

        if (hasUserCondition) {
            LambdaQueryWrapper<ClientUser> userQw = new LambdaQueryWrapper<>();
            if (ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getName())) {
                userQw.like(ClientUser::getName, bizStudentProfilePageParam.getName());
            }
            if (ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getAccount())) {
                userQw.like(ClientUser::getAccount, bizStudentProfilePageParam.getAccount());
            }
            if (ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getPhone())) {
                userQw.like(ClientUser::getPhone, bizStudentProfilePageParam.getPhone());
            }
            if (ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getSearchKey())) {
                String sk = bizStudentProfilePageParam.getSearchKey();
                userQw.and(w -> w.like(ClientUser::getName, sk).or().like(ClientUser::getAccount, sk));
            }
            List<ClientUser> matchedUsers = clientUserService.list(userQw);
            matchedUserIds = matchedUsers.stream().map(ClientUser::getId).collect(Collectors.toSet());
        }

        if (ObjectUtil.isNotEmpty(bizStudentProfilePageParam.getSearchKey())) {
            String sk = bizStudentProfilePageParam.getSearchKey();
            if (matchedUserIds != null && !matchedUserIds.isEmpty()) {
                Set<String> finalUserIds = matchedUserIds;
                queryWrapper.lambda().and(w -> w.like(BizStudentProfile::getStudentNo, sk)
                        .or().like(BizStudentProfile::getMajor, sk)
                        .or().in(BizStudentProfile::getUserId, finalUserIds));
            } else {
                queryWrapper.lambda().and(w -> w.like(BizStudentProfile::getStudentNo, sk)
                        .or().like(BizStudentProfile::getMajor, sk));
            }
        } else if (hasUserCondition) {
            if (matchedUserIds == null || matchedUserIds.isEmpty()) {
                return new Page<>();
            }
            queryWrapper.lambda().in(BizStudentProfile::getUserId, matchedUserIds);
        }

        if(ObjectUtil.isAllNotEmpty(bizStudentProfilePageParam.getSortField(), bizStudentProfilePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizStudentProfilePageParam.getSortOrder());
            queryWrapper.orderBy(true, bizStudentProfilePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizStudentProfilePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(BizStudentProfile::getCreateTime);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        Page<BizStudentProfile> pageResult = this.page(CommonPageRequest.defaultPage(), queryWrapper);
        fillClientUserInfo(pageResult.getRecords());
        return pageResult;
    }

    private void fillClientUserInfo(List<BizStudentProfile> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        Set<String> userIds = list.stream().map(BizStudentProfile::getUserId)
                .filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        List<ClientUser> users = clientUserService.listByIds(userIds);
        Map<String, ClientUser> userMap = CollStreamUtil.toIdentityMap(users, ClientUser::getId);
        for (BizStudentProfile profile : list) {
            ClientUser user = userMap.get(profile.getUserId());
            if (user != null) {
                profile.setName(user.getName());
                profile.setAccount(user.getAccount());
                profile.setPhone(user.getPhone());
                profile.setEmail(user.getEmail());
                profile.setAvatar(user.getAvatar());
                profile.setUserStatus(user.getUserStatus());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizStudentProfileAddParam bizStudentProfileAddParam) {
        BizStudentProfile bizStudentProfile = BeanUtil.toBean(bizStudentProfileAddParam, BizStudentProfile.class);
        if (StrUtil.isBlank(bizStudentProfile.getStudentNo())) {
            throw new CommonException("学号不能为空");
        }
        bizStudentProfile.setStudentNo(bizStudentProfile.getStudentNo().trim());
        long count = this.count(new LambdaQueryWrapper<BizStudentProfile>()
                .eq(BizStudentProfile::getStudentNo, bizStudentProfile.getStudentNo())
                .eq(BizStudentProfile::getDeleteFlag, "NOT_DELETE"));
        if (count > 0) {
            throw new CommonException("该学号【" + bizStudentProfile.getStudentNo() + "】已存在，请勿重复添加");
        }
        if (StrUtil.isNotBlank(bizStudentProfileAddParam.getName())) {
            JSONObject ext = StrUtil.isNotBlank(bizStudentProfile.getExtJson()) && JSONUtil.isTypeJSONObject(bizStudentProfile.getExtJson())
                    ? JSONUtil.parseObj(bizStudentProfile.getExtJson()) : new JSONObject();
            ext.set("name", bizStudentProfileAddParam.getName().trim());
            bizStudentProfile.setExtJson(ext.toString());
        }
        this.save(bizStudentProfile);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizStudentProfileEditParam bizStudentProfileEditParam) {
        BizStudentProfile bizStudentProfile = this.queryEntity(bizStudentProfileEditParam.getId());
        BeanUtil.copyProperties(bizStudentProfileEditParam, bizStudentProfile);
        this.updateById(bizStudentProfile);

        if (StrUtil.isNotBlank(bizStudentProfile.getUserId())) {
            ClientUser user = clientUserService.getById(bizStudentProfile.getUserId());
            if (user != null) {
                boolean changed = false;
                if (StrUtil.isNotBlank(bizStudentProfileEditParam.getName())) {
                    user.setName(bizStudentProfileEditParam.getName());
                    user.setNickname(bizStudentProfileEditParam.getName());
                    changed = true;
                }
                if (StrUtil.isNotBlank(bizStudentProfileEditParam.getPhone())) {
                    user.setPhone(bizStudentProfileEditParam.getPhone());
                    changed = true;
                }
                if (StrUtil.isNotBlank(bizStudentProfileEditParam.getEmail())) {
                    user.setEmail(bizStudentProfileEditParam.getEmail());
                    changed = true;
                }
                if (changed) {
                    clientUserService.updateById(user);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizStudentProfileIdParam> bizStudentProfileIdParamList) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限删除学生档案管理");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 根据业务需要，取消下面注释并修改为实际的数据范围字段，例如对删除的记录进行数据范围校验
            // Set<String> orgIdSet = this.listByIds(CollStreamUtil.toList(bizStudentProfileIdParamList, BizStudentProfileIdParam::getId))
            //         .stream().map(BizStudentProfile::getOrgId).collect(Collectors.toSet());
            // if(!new HashSet<>(loginUserDataScope).containsAll(orgIdSet)) {
            //     throw new CommonException("您没有权限删除这些机构下的学生档案管理");
            // }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizStudentProfileIdParamList, BizStudentProfileIdParam::getId));
    }

    @Override
    public BizStudentProfile detail(BizStudentProfileIdParam bizStudentProfileIdParam) {
        BizStudentProfile profile = this.queryEntity(bizStudentProfileIdParam.getId());
        if (StrUtil.isNotBlank(profile.getUserId())) {
            ClientUser user = clientUserService.getById(profile.getUserId());
            if (user != null) {
                profile.setName(user.getName());
                profile.setAccount(user.getAccount());
                profile.setPhone(user.getPhone());
                profile.setEmail(user.getEmail());
                profile.setAvatar(user.getAvatar());
                profile.setUserStatus(user.getUserStatus());
            }
        }
        return profile;
    }

    @Override
    public BizStudentProfile queryEntity(Serializable id) {
        BizStudentProfile bizStudentProfile = this.getById(id);
        if(ObjectUtil.isEmpty(bizStudentProfile)) {
            throw new CommonException("学生档案管理不存在，id值为：{}", id);
        }
        return bizStudentProfile;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        File tempFile = null;
        try {
            List<BizStudentProfileImportParam> sampleList = CollectionUtil.newArrayList();
            BizStudentProfileImportParam sample = new BizStudentProfileImportParam();
            sample.setStudentNo("202608001");
            sample.setName("李华");
            sample.setMajor("软件工程");
            sample.setClassName("软件工程卓越班");
            sample.setGrade("2023级");
            sample.setSkillTags("Java,Python,Vue3,算法与数据结构");
            sample.setBio("热爱学科竞赛与算法研究，具备良好的团队协作能力。");
            sampleList.add(sample);

            String fileName = "学生档案管理导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
            EasyExcel.write(tempFile.getPath(), BizStudentProfileImportParam.class).sheet("学生档案名册").doWrite(sampleList);
            CommonDownloadUtil.download(tempFile, response);
        } catch (Exception e) {
            log.error(">>> 学生档案管理导入模板下载失败：", e);
            CommonResponseUtil.renderError(response, "学生档案管理导入模板下载失败");
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
            File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() +
                    FileUtil.FILE_SEPARATOR + "bizStudentProfileImportTemplate_" + System.currentTimeMillis() + ".xlsx"));
            List<BizStudentProfileImportParam> list = EasyExcel.read(tempFile).head(BizStudentProfileImportParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            for (int i = 0; i < list.size(); i++) {
                BizStudentProfileImportParam p = list.get(i);
                if (p == null || StrUtil.isBlank(p.getStudentNo())) {
                    errorCount += 1;
                    errorDetail.add(JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "学号不能为空"));
                    continue;
                }
                String studentNo = p.getStudentNo().trim();
                BizStudentProfile exist = this.getOne(new LambdaQueryWrapper<BizStudentProfile>()
                        .eq(BizStudentProfile::getStudentNo, studentNo)
                        .eq(BizStudentProfile::getDeleteFlag, "NOT_DELETE"), false);
                if (exist == null) {
                    exist = new BizStudentProfile();
                    exist.setId(IdWorker.getIdStr());
                    exist.setStudentNo(studentNo);
                    exist.setDeleteFlag("NOT_DELETE");
                }
                if (StrUtil.isNotBlank(p.getMajor())) exist.setMajor(p.getMajor().trim());
                if (StrUtil.isNotBlank(p.getClassName())) exist.setClassName(p.getClassName().trim());
                if (StrUtil.isNotBlank(p.getGrade())) exist.setGrade(p.getGrade().trim());
                if (StrUtil.isNotBlank(p.getSkillTags())) exist.setSkillTags(p.getSkillTags().trim());
                if (StrUtil.isNotBlank(p.getBio())) exist.setBio(p.getBio().trim());
                if (StrUtil.isNotBlank(p.getName())) {
                    JSONObject ext = StrUtil.isNotBlank(exist.getExtJson()) && JSONUtil.isTypeJSONObject(exist.getExtJson())
                            ? JSONUtil.parseObj(exist.getExtJson()) : new JSONObject();
                    ext.set("name", p.getName().trim());
                    exist.setExtJson(ext.toString());
                }
                this.saveOrUpdate(exist);
                successCount += 1;
            }
            FileUtil.del(tempFile);
            return JSONUtil.createObj()
                    .set("totalCount", list.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 学生档案管理导入失败：", e);
            throw new CommonException("学生档案管理导入失败：" + e.getMessage());
        }
    }

    @Override
    public void exportData(List<BizStudentProfileIdParam> bizStudentProfileIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<BizStudentProfileEditParam> dataList;
         if(ObjectUtil.isNotEmpty(bizStudentProfileIdParamList)) {
            List<Serializable> idList = CollStreamUtil.toList(bizStudentProfileIdParamList, BizStudentProfileIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), BizStudentProfileEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), BizStudentProfileEditParam.class);
         }
         String fileName = "学生档案管理_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), BizStudentProfileEditParam.class).sheet("学生档案管理").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 学生档案管理导出失败：", e);
         CommonResponseUtil.renderError(response, "学生档案管理导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
