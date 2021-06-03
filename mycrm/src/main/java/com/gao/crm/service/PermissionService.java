package com.gao.crm.service;

import com.gao.crm.base.BaseService;
import com.gao.crm.dao.PermissionMapper;
import com.gao.crm.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {
    @Resource
    private PermissionMapper permissionMapper;

    public List<String> queryUserHasRolesHasPermissions(Integer userId){
        return permissionMapper.queryUserHasRolesHasPermissions(userId);
    }
}
