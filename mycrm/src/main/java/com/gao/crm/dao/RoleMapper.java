package com.gao.crm.dao;

import com.gao.crm.base.BaseMapper;
import com.gao.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {

    //查询角色列表
    List<Map<String,Object>> queryAllRoles(Integer userId);

    Role queryRoleByRoleName(String roleName);
}