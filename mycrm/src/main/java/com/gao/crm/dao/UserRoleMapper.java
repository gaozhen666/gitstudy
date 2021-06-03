package com.gao.crm.dao;

import com.gao.crm.base.BaseMapper;
import com.gao.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    Integer countUserRoleByUserId(Integer userId);

    Integer deleteUserRoleByUserId(Integer userId);

}