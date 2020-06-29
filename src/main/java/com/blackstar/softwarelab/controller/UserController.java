package com.blackstar.softwarelab.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.SysUser;
import com.blackstar.softwarelab.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {

    @Autowired
    private ISysUserService userService;

    @Value("user.admin.id")
    private String adminId;

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public SysUser get(@PathVariable String id) {
        return userService.getById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public SysUser add(SysUser user) {
        userService.save(user);
        return user;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public SysUser update(SysUser user) {
        userService.updateById(user);
        return userService.getById(user.getId());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public boolean remove(@PathVariable String id) {
        if(!adminId.equals(id)){
            return false;
        }
        SysUser user = new SysUser();
        user.setId(id);
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(DbConst.COLUMN_STATUS, DbConst.STATUS_DELETE);
        return userService.update(user,updateWrapper);
    }

    public boolean delete(@PathVariable String id){
        if(!adminId.equals(id)){
            return false;
        }
        return userService.removeById(id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public IPage<SysUser> list(@RequestParam int pageNum, @RequestParam int pageSize) {

        Page<SysUser> userPage = new Page<>(pageNum, pageSize);

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DbConst.COLUMN_STATUS, DbConst.STATUS_NORMAL);

        return userService.page(userPage, queryWrapper);
    }

}
