package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IUserServiceTest {
    @Autowired
    private IUserService userService;


    @Test
    void testSaveUser(){
        User user = new User();
        user.setId(7L);
        user.setUsername("Lilei");
        user.setPassword("123");
        user.setPhone("18688990011L");
        user.setBalance(200);
        user.setInfo(UserInfo.of(24,"英文老师","female"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.save(user);
    }

    @Test
    void testQuery(){
        List<User> users = userService.listByIds(List.of(1L, 2L, 3L));
        users.forEach(System.out::println);
    }

    @Test
    void testPageQuery(){
        int pageNo = 1,pageSize = 2;
        //1.准备分页条件
        //1.1分页条件
        Page<User> page = Page.of(pageNo, pageSize);
        //1.2排序条件
        //可以有多个排序标准
        //先按余额排，如果余额相同再按id排
        page.addOrder(new OrderItem("balance", true));
        page.addOrder(new OrderItem("id", true));
        //2.分页查询
        Page<User> p = userService.page(page);

        //3.解析
        //总条数
        long total = p.getTotal();
        System.out.println(total);
        //总页数
        long pages = p.getPages();
        System.out.println(pages);
        //分页的数据
        List<User> records = p.getRecords();
        records.forEach(System.out::println);
    }
}