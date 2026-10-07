package vip.xiaonuo.biz.modular.portalmgr.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.portalmgr.entity.BizForumBoard;

import java.util.List;

/**
 * 社区论坛板块Service接口
 *
 * @author antigravity
 * @date 2026/10/05
 **/
public interface BizForumBoardService extends IService<BizForumBoard> {

    /**
     * 分页查询板块
     */
    Page<BizForumBoard> page(int current, int size, String searchKey);

    /**
     * 获取所有启用的板块列表（提供给前台展示）
     */
    List<BizForumBoard> listEnabledBoards();

    /**
     * 新增板块
     */
    void add(BizForumBoard board);

    /**
     * 编辑板块
     */
    void edit(BizForumBoard board);

    /**
     * 删除板块
     */
    void delete(List<String> ids);
}
