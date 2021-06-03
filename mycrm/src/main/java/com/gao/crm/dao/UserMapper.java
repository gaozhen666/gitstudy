package com.gao.crm.dao;

import com.gao.crm.base.BaseMapper;
import com.gao.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {
    Integer deleteByPrimaryKey(Integer id);

    Integer insert(User record);

    Integer insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKeySelective(User record);

    Integer updateByPrimaryKey(User record);

    User queryUserByUserName(String userName);

    //查询所有的销售人员
    List<Map<String,Object>> queryAllSales();

}