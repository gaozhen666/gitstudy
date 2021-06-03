package com.gao.crm.service;

import com.gao.crm.base.BaseService;
import com.gao.crm.dao.ModuleMapper;
import com.gao.crm.dao.PermissionMapper;
import com.gao.crm.dao.RoleMapper;
import com.gao.crm.query.RoleQuery;
import com.gao.crm.utils.AssertUtil;
import com.gao.crm.vo.Module;
import com.gao.crm.vo.Permission;
import com.gao.crm.vo.Role;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    //查询角色列表
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    //多条件分页查询
    public Map<String,Object> queryRoleByParams(RoleQuery roleQuery){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        List<Role> list = roleMapper.selectByParams(roleQuery);
        PageInfo<Role> plist = new PageInfo<>(list);
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }

    //添加角色
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"该用户已存在");
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.insertSelective(role)<1,"角色记录添加失败");
    }

    //更新角色
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        AssertUtil.isTrue(null==role.getId() || null==roleMapper.selectByPrimaryKey(role.getId()),"待修改的记录不存在");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null!=temp && !role.getId().equals(temp.getId()),"该角色已存在");
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色记录更新失败");
    }

    //删除角色
    public void deleteRole(Integer roleId){
        Role role = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId || null==role,"待删除的记录不存在");
        role.setIsValid(0);
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色记录删除失败");
    }

    //添加权限记录
    public void addGrant(Integer[] mids,Integer roleId){
        /**
         * 核心表-t_permission  t_role(校验角色存在)
         *   如果角色存在原始权限  删除角色原始权限
         *     然后添加角色新的权限 批量添加权限记录到t_permission
         */
        Role temp = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==roleId || null==temp,"待授权的角色不存在");
        Integer count = permissionMapper.countPermissionByRoleId(roleId);
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByRoleId(roleId)<count,"权限分配失败");
        }
        if (null!=mids && mids.length>0){
            List<Permission> permissions = new ArrayList<>();
            for (Integer mid:mids){
                Permission permission = new Permission();
                permission.setCreateDate(new Date());
                permission.setCreateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                Module module = moduleMapper.selectByPrimaryKey(mid);
                permission.setAclValue(module.getOptValue());
                permissions.add(permission);
            }
            permissionMapper.insertBatch(permissions);
        }
    }

}
