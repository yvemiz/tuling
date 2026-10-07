package vip.xiaonuo.biz.modular.portalmgr.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.portalmgr.entity.BizPortalConfig;

/**
 * 前台门户配置Service接口
 *
 * @author antigravity
 * @date 2026/10/05
 **/
public interface BizPortalConfigService extends IService<BizPortalConfig> {

    /**
     * 获取前台所有整合配置
     */
    JSONObject getAllConfigs();

    /**
     * 保存单个配置项
     */
    void saveConfig(String configKey, String configName, String configValue);
}
