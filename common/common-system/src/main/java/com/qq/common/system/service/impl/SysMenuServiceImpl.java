package com.qq.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qq.common.core.exception.ServiceException;
import com.qq.common.system.mapper.SysRoleMenuMapper;
import com.qq.common.system.pojo.SysMenu;
import com.qq.common.system.pojo.SysRoleMenu;
import com.qq.common.system.service.SysMenuService;
import com.qq.common.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author QinQiang
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2022-04-29 14:43:18
*/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{

    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public void addMenu(SysMenu sysMenu, List<SysRoleMenu> sysRoleMenus) {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<SysMenu>().eq("menu_id", sysMenu.getMenuId()));
        if (count > 0) {
            throw new ServiceException("菜单编号已存在");
        }
        baseMapper.insert(sysMenu);
        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }
}




