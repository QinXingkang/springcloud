package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements IUserService {
    @Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        //1.查询用户
        User user = getById(id);

        //2.校验用户状态
        if(user==null || user.getStatus() == UserStatus.FROZEN){
            throw new RuntimeException("用户状态异常！");
        }

        //3.校验余额是否充足
        if(user.getBalance() < money){
            throw new RuntimeException("用户余额不足");
        }

        //4.扣减余额 update tb_user set balance = balance - ?
//        baseMapper.deductBalance(id, money);
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0,User::getStatus, UserStatus.FROZEN)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance())
                .update();
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer maxBalance, Integer minBalance) {

        List<User> users = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .ge(minBalance != null, User::getBalance, minBalance)
                .list();

        return users;
    }

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        //1.查询用户
        User user = getById(id);
        if(user==null || user.getStatus() == UserStatus.FROZEN){
            throw new RuntimeException("用户状态异常！");
        }

        //2.查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, id)
                .list();

        //3.封装VO
        //3.1转User的PO为VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        //3.2转地址VO
        if(CollUtil.isNotEmpty(addresses)){
            List<AddressVO> addressVOS = BeanUtil.copyToList(addresses, AddressVO.class);
            userVO.setAddresses(addressVOS);
        }
        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        //1.查询用户
        List<User> users = listByIds(ids);
        if(CollUtil.isEmpty(users)){
            return Collections.emptyList();
        }

        //2.查询地址
        //2.1获取用户id集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        //2.2根据用户id查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIds).list();

        //2.3转换地址VO
        List<AddressVO> addressVOList = BeanUtil.copyToList(addresses, AddressVO.class);
        //2.4用户地址集合分组处理，相同用户的放入一个集合中
        Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
        if(CollUtil.isNotEmpty(addressVOList)){
            addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }


        //3.转换VO返回
        List<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            //3.1转换User的PO为VO
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            list.add(userVO);

            //3.2转换地址VO
            userVO.setAddresses(addressMap.get(user.getId()));
        }
        return list;
    }

    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        String name = query.getName();
        Integer status = query.getStatus();

        //1.构件分页条件
//        //1.1分页条件
//        Page<User> page = Page.of(query.getPageNo(), query.getPageSize());
//        //1.2排序条件
//        if(StrUtil.isNotBlank(query.getSortBy())){
//            //不为空
//            page.addOrder(new OrderItem(query.getSortBy(),query.getIsAsc()));
//        }else {
//            //为空,默认按照更新时间排序
//            page.addOrder(new OrderItem("update_time",false));
//        }
        Page<User> page = query.toMpPageDefaultSortByUpdateTime();


        //2.分页查询
        Page<User> p = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .page(page);

        //3.封装VO结果
//        PageDTO<UserVO> dto = new PageDTO<>();
//        //3.1总条数
//        dto.setTotal(p.getTotal());
//        //3.2总页数
//        dto.setPages(p.getPages());
//        //3.3当前页数据
//        List<User> records = p.getRecords();
//        if(CollUtil.isEmpty(records)){
//            dto.setList(Collections.emptyList());
//            return dto;
//        }
//        //3.4拷贝user的VO
//        List<UserVO> userVOS = BeanUtil.copyToList(records, UserVO.class);
//        dto.setList(userVOS);
//        //4.返回
//        return dto;

        return PageDTO.of(p,user -> {
            //1.拷贝基础属性
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);

            //2.处理特殊逻辑
            vo.setUsername(vo.getUsername().substring(0,vo.getUsername().length() - 2) + "**");
            return vo;
        });
    }
}
