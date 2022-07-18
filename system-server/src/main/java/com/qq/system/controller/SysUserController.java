package com.qq.system.controller;

import com.qq.common.core.web.domain.AjaxResult;
import com.qq.common.log.annotation.Log;
import com.qq.common.system.pojo.SysUser;
import com.qq.common.system.pojo.SysUserAddress;
import com.qq.system.service.SysRoleService;
import com.qq.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息表(SysUser)表控制层
 *
 * @author makejava
 * @since 2022-04-07 19:08:25
 */
@RestController
@RequestMapping("system/sysUser")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysUserController {
    /**
     * 服务对象
     */
    private final SysUserService sysUserService;

    /**
     * 服务对象
     */
    private final SysRoleService sysRoleService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("getById")
    @Log(title = "system", funcDesc = "查询单个用户")
    public AjaxResult selectOne(@RequestParam("id") Long id) {
        return AjaxResult.success(sysUserService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param pageNum
     * @param pageSize
     * @return 查询结果
     */
    @Log(title = "system", funcDesc = "获取用户列表")
    @GetMapping("list")
    public AjaxResult selectAll(int pageNum, int pageSize) {
        return AjaxResult.success(sysUserService.queryAllByLimit(pageNum, pageSize));
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @Log(title = "system", funcDesc = "修改用户信息")
    @PostMapping("update")
    public AjaxResult update(@RequestBody SysUser user) {
        return AjaxResult.success(sysUserService.update(user));
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @Log(title = "system", funcDesc = "新增用户")
    @PutMapping("add")
    public AjaxResult add(@RequestBody SysUser user) {
        return AjaxResult.success(sysUserService.insert(user));
    }

    /**
     * 获取用户角色
     *
     * @param userId
     * @return
     */
    @Log(title = "system", funcDesc = "获取用户角色")
    @GetMapping("getRolesByUser")
    public AjaxResult getRolesByUser(@RequestParam("userId") Long userId) {
        return AjaxResult.success(sysRoleService.getByUser(userId));
    }

    /**
     * 修改用户头像
     *
     * @param userId
     * @param file
     * @return
     */
    @Log(title = "system", funcDesc = "修改用户头像")
    @PostMapping("modifyAvatar")
    public AjaxResult modifyAvatar(@RequestParam("userId") Long userId, @RequestParam("file") MultipartFile file) {
        sysUserService.modifyAvatar(userId, file);
        return AjaxResult.success();
    }

    /**
     * 新增用户地址
     *
     * @param address
     */
    @Log(title = "system", funcDesc = "新增用户地址")
    @PutMapping("addUserAddress")
    public AjaxResult addUserAddress(@RequestBody SysUserAddress address) {
        sysUserService.addUserAddress(address);
        return AjaxResult.success();
    }

    /**
     * 修改用户地址
     *
     * @param address
     */
    @Log(title = "system", funcDesc = "修改用户地址")
    @PostMapping("modifyUserAddress")
    public AjaxResult modifyUserAddress(@RequestBody SysUserAddress address) {
        sysUserService.modifyUserAddress(address);
        return AjaxResult.success();
    }

    /**
     * 查询用户地址
     *
     * @param userId
     */
    @Log(title = "system", funcDesc = "查询用户地址")
    @GetMapping("queryUserAddress")
    public AjaxResult queryUserAddress(@RequestParam("userId") Long userId) {
        return AjaxResult.success(sysUserService.queryUserAddress(userId));
    }

    /**
     * 查询用户默认地址
     *
     * @param userId
     */
    @Log(title = "system", funcDesc = "查询用户默认地址")
    @GetMapping("userDefaultAddress")
    public AjaxResult userDefaultAddress(@RequestParam("userId") Long userId) {
        return AjaxResult.success(sysUserService.queryUserDefaultAddress(userId));
    }

    /**
     * 根据id查询地址
     *
     * @param id
     */
    @Log(title = "system", funcDesc = "根据id查询地址")
    @GetMapping("queryAddressById")
    public AjaxResult queryAddressById(@RequestParam("id") Long id) {
        return AjaxResult.success(sysUserService.queryAddressById(id));
    }

    /**
     * 删除用户地址
     *
     * @param id
     */
    @Log(title = "system", funcDesc = "删除用户地址")
    @DeleteMapping("deleteUserAddress")
    public AjaxResult deleteUserAddress(Long id) {
        sysUserService.deleteUserAddress(id);
        return AjaxResult.success();
    }
}