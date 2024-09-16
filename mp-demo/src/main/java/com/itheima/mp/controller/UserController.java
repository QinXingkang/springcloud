package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    //这里通过加 final 和 @RequiredArgsConstructor来完成注入
    //后续非注入的成员变量 无需加final 即可
    private final IUserService userService;

//    //使用构造函数注入（Spring推荐）
//    public UserController(IUserService userService) {
//        this.userService = userService;
//    }

    /**
     * 新增用户
     * @param userFormDTO
     */
    @PostMapping
    @ApiOperation("新增用户接口")
    public void save(@RequestBody UserFormDTO userFormDTO){
        //1.把DTO拷贝到PO
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        //2.新增
        userService.save(user);
    }

    /**
     * 根据id删除用户
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户接口")
    public void deleteUserById(@ApiParam("用户id") @PathVariable("id") Long id){
        userService.removeById(id);
    }

    /**
     * 根据id查询用户
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户接口")
    public UserVO queryUserById(@ApiParam("用户id") @PathVariable("id") Long id){

        //代码改造

//        //1.查询用户PO
//        User user = userService.getById(id);
//        //2.把PO拷贝到VO返回
//        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
//
//        return userVO;

        return userService.queryUserAndAddressById(id);
    }

    @GetMapping
    @ApiOperation("根据id批量查询用户接口")
    public List<UserVO> queryUserById(@ApiParam("用户id集合") @RequestParam("ids") List<Long> ids){

        //代码改造

//        //1.查询用户PO
//        List<User> users = userService.listByIds(ids);
//        //2.把PO拷贝到VO返回
//        List<UserVO> userVOS = BeanUtil.copyToList(users, UserVO.class);
//
//        return userVOS;

        return userService.queryUserAndAddressByIds(ids);
    }

    @PutMapping("/{id}/deduction/{money}")
    @ApiOperation("扣减用户余额接口")
    public void deductMoneyById(
            @ApiParam("用户id") @PathVariable("id") Long id,
            @ApiParam("扣减金额") @PathVariable("money") Integer money){
        userService.deductBalance(id, money);
    }

    @GetMapping("/list")
    @ApiOperation("根据复杂条件查询用户接口")
    public List<UserVO> queryUsers(UserQuery userQuery){
        //1.查询用户PO
        List<User> users = userService.queryUsers(userQuery.getName(), userQuery.getStatus(), userQuery.getMaxBalance(), userQuery.getMinBalance());
        //2.把PO拷贝到VO返回
        List<UserVO> userVOS = BeanUtil.copyToList(users, UserVO.class);

        return userVOS;
    }

    @GetMapping("/page")
    @ApiOperation("根据条件分页查询用户接口")
    public PageDTO<UserVO> queryUsersPage(UserQuery userQuery){
//        //1.查询用户PO
//        List<User> users = userService.queryUsers(userQuery.getName(), userQuery.getStatus(), userQuery.getMaxBalance(), userQuery.getMinBalance());
//        //2.把PO拷贝到VO返回
//        List<UserVO> userVOS = BeanUtil.copyToList(users, UserVO.class);

        return userService.queryUsersPage(userQuery);
    }

}
