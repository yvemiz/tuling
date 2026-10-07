package vip.xiaonuo.biz.modular.portalmgr.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.portalmgr.entity.BizPortalConfig;
import vip.xiaonuo.biz.modular.portalmgr.mapper.BizPortalConfigMapper;
import vip.xiaonuo.biz.modular.portalmgr.service.BizPortalConfigService;

import java.util.List;

/**
 * 前台门户配置Service实现类
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Service
public class BizPortalConfigServiceImpl extends ServiceImpl<BizPortalConfigMapper, BizPortalConfig> implements BizPortalConfigService {

    @Override
    public JSONObject getAllConfigs() {
        List<BizPortalConfig> list = this.list(new LambdaQueryWrapper<BizPortalConfig>()
                .eq(BizPortalConfig::getDeleteFlag, "NOT_DELETE")
                .orderByAsc(BizPortalConfig::getSortCode));
        JSONObject result = new JSONObject();
        for (BizPortalConfig cfg : list) {
            String val = cfg.getConfigValue();
            if (JSONUtil.isTypeJSONObject(val)) {
                result.set(cfg.getConfigKey(), JSONUtil.parseObj(val));
            } else if (JSONUtil.isTypeJSONArray(val)) {
                result.set(cfg.getConfigKey(), JSONUtil.parseArray(val));
            } else {
                result.set(cfg.getConfigKey(), val);
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveConfig(String configKey, String configName, String configValue) {
        BizPortalConfig config = this.getOne(new LambdaQueryWrapper<BizPortalConfig>()
                .eq(BizPortalConfig::getConfigKey, configKey));
        if (config == null) {
            config = new BizPortalConfig();
            config.setConfigKey(configKey);
            config.setConfigName(configName);
            config.setConfigValue(configValue);
            this.save(config);
        } else {
            if (ObjectUtil.isNotEmpty(configName)) {
                config.setConfigName(configName);
            }
            config.setConfigValue(configValue);
            this.updateById(config);
        }
    }
}
