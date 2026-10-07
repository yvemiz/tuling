package vip.xiaonuo.biz.modular.portalmgr.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.portalmgr.entity.BizForumBoard;
import vip.xiaonuo.biz.modular.portalmgr.mapper.BizForumBoardMapper;
import vip.xiaonuo.biz.modular.portalmgr.service.BizForumBoardService;
import vip.xiaonuo.common.exception.CommonException;

import java.util.List;

/**
 * 社区论坛板块Service实现类
 *
 * @author antigravity
 * @date 2026/10/05
 **/
@Service
public class BizForumBoardServiceImpl extends ServiceImpl<BizForumBoardMapper, BizForumBoard> implements BizForumBoardService {

    @Override
    public Page<BizForumBoard> page(int current, int size, String searchKey) {
        Page<BizForumBoard> pageParam = new Page<>(current, size);
        LambdaQueryWrapper<BizForumBoard> qw = new LambdaQueryWrapper<BizForumBoard>()
                .eq(BizForumBoard::getDeleteFlag, "NOT_DELETE");
        if (StrUtil.isNotBlank(searchKey)) {
            qw.and(w -> w.like(BizForumBoard::getBoardName, searchKey)
                    .or().like(BizForumBoard::getBoardCode, searchKey)
                    .or().like(BizForumBoard::getDescription, searchKey));
        }
        qw.orderByAsc(BizForumBoard::getSortCode);
        return this.page(pageParam, qw);
    }

    @Override
    public List<BizForumBoard> listEnabledBoards() {
        return this.list(new LambdaQueryWrapper<BizForumBoard>()
                .eq(BizForumBoard::getDeleteFlag, "NOT_DELETE")
                .eq(BizForumBoard::getStatus, "ENABLE")
                .orderByAsc(BizForumBoard::getSortCode));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizForumBoard board) {
        if (StrUtil.isBlank(board.getBoardCode()) || StrUtil.isBlank(board.getBoardName())) {
            throw new CommonException("板块名称和编码不能为空");
        }
        long count = this.count(new LambdaQueryWrapper<BizForumBoard>()
                .eq(BizForumBoard::getBoardCode, board.getBoardCode())
                .eq(BizForumBoard::getDeleteFlag, "NOT_DELETE"));
        if (count > 0) {
            throw new CommonException("板块编码已存在，请使用其他编码");
        }
        if (StrUtil.isBlank(board.getStatus())) {
            board.setStatus("ENABLE");
        }
        if (StrUtil.isBlank(board.getPostPermission())) {
            board.setPostPermission("ALL");
        }
        this.save(board);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizForumBoard board) {
        if (StrUtil.isBlank(board.getId())) {
            throw new CommonException("板块ID不能为空");
        }
        BizForumBoard exist = this.getById(board.getId());
        if (exist == null) {
            throw new CommonException("板块不存在");
        }
        this.updateById(board);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<String> ids) {
        if (ObjectUtil.isNotEmpty(ids)) {
            this.removeByIds(ids);
        }
    }
}
