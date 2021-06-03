package com.gao.crm.service;

import com.gao.crm.base.BaseService;
import com.gao.crm.dao.ModuleMapper;
import com.gao.crm.dao.PermissionMapper;
import com.gao.crm.dto.TreeDto;
import com.gao.crm.utils.AssertUtil;
import com.gao.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    public List<TreeDto> queryAllModules(){
        return moduleMapper.queryAllModules();
    }

    public List<TreeDto> queryAllModules02(Integer roleId){
        List<TreeDto> treeDtos = moduleMapper.queryAllModules();
        //根据角色id 查询角色拥有的菜单id  List<Integer>
        List<Integer> roleHasMids = permissionMapper.queryRoleHasAllModuleIdsByRoleId(roleId);
        if (null!=roleHasMids && roleHasMids.size()>0){
            treeDtos.forEach(treeDto -> {
                if (roleHasMids.contains(treeDto.getId())){
                    //说明当前角色 分配了该菜单
                    treeDto.setChecked(true);
                }
            });
        }
        return treeDtos;
    }

    public Map<String,Object> moduleList(){
        Map<String,Object> result = new HashMap<>();
        List<Module> modules = moduleMapper.queryModules();
        result.put("count",modules.size());
        result.put("data",modules);
        result.put("code",0);
        result.put("msg","");
        return result;
    }

    /**
     * 1.参数校验
     *     模块名-module_name
     *         非空  同一层级下模块名唯一
     *     url
     *         二级菜单  非空  不可重复
     *     上级菜单-parent_id
     *         一级菜单   null
     *         二级|三级菜单 parent_id 非空 必须存在
     *      层级-grade
     *          非空  0|1|2
     *       权限码 optValue
     *          非空  不可重复
     * 2.参数默认值设置
     *     is_valid  create_date update_date
     * 3.执行添加 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveModule(Module module){
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输入菜单名");
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null==grade || !(grade==0||grade==1||grade==2),"菜单层级不合法");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndModuleName(module.getGrade(),module.getModuleName()),"该层级下菜单重复");
        if (grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定二级菜单的url值");
            AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndUrl(module.getGrade(),module.getUrl()),"二级菜单url不可重复");
        }
        if (grade!=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null==moduleMapper.selectByPrimaryKey(parentId),"请指定上级菜单");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复");
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.insertSelective(module)<1,"菜单添加失败");
    }

    /**
     * 1.参数校验
     *     id 非空 记录存在
     *     模块名-module_name
     *         非空  同一层级下模块名唯一
     *     url
     *         二级菜单  非空  不可重复
     *     上级菜单-parent_id
     *         二级|三级菜单 parent_id 非空 必须存在
     *      层级-grade
     *          非空  0|1|2
     *       权限码 optValue
     *          非空  不可重复
     * 2.参数默认值设置
     *      update_date
     * 3.执行更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        AssertUtil.isTrue(null==module.getId() || null==moduleMapper.selectByPrimaryKey(module.getId()),"待更新的记录不存在");
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请指定菜单名称");
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null==grade || !(grade==0||grade==1||grade==2),"菜单层级不合法");
        Module temp = moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if (null!=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下菜单已存在");
        }
        if (grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定二级菜单的url值");
            temp = moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            if (null!=temp){
                AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下url已存在");
            }
        }
        if (grade!=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null==moduleMapper.selectByPrimaryKey(parentId),"请指定上级菜单");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        if (null!=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"权限码已存在");
        }
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"菜单更新失败");
    }

    public List<Map<String, Object>> queryAllModulesByGrade(Integer grade) {
        return moduleMapper.queryAllModulesByGrade(grade);
    }

    //删除菜单记录
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModuleById(Integer mid){
        Module temp = moduleMapper.selectByPrimaryKey(mid);
        AssertUtil.isTrue(null==mid || null==temp,"待删除的记录不存在");
        //如果存在子菜单，不允许删除
        Integer count = moduleMapper.countSubModuleByParentId(mid);
        AssertUtil.isTrue(count>0,"存在子菜单，不支持删除操作");
        //权限表
        count = permissionMapper.countPermissionsByModuleId(mid);
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByModuleId(mid)<count,"菜单删除失败");
        }
        temp.setIsValid((byte)0);
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp)<1,"菜单删除失败");
    }
}
