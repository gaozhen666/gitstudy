package com.gao.crm.dao;

import com.gao.crm.base.BaseMapper;
import com.gao.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    Integer countPermissionByRoleId(Integer roleId);

    Integer deletePermissionsByRoleId(Integer roleId);

    List<Integer> queryRoleHasAllModuleIdsByRoleId(Integer roleId);

    List<String> queryUserHasRolesHasPermissions(Integer userId);

    Integer countPermissionsByModuleId(Integer moduleId);

    Integer deletePermissionsByModuleId(Integer moduleId);
}