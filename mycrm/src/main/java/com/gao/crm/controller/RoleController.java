package com.gao.crm.controller;

import com.gao.crm.base.BaseController;
import com.gao.crm.base.ResultInfo;
import com.gao.crm.query.RoleQuery;
import com.gao.crm.service.RoleService;
import com.gao.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    @RequestMapping("role/queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    @RequestMapping("role/index")
    public String index(){
        return "role/role";
    }

    //多条件分页查询
    @RequestMapping("role/list")
    @ResponseBody
    public Map<String,Object> queryRoleByParams(RoleQuery roleQuery){
        Map<String, Object> map = roleService.queryRoleByParams(roleQuery);
        return map;
    }

    //添加及更新页面跳转
    @RequestMapping("role/addOrUpdateRolePage")
    public String addOrUpdateRolePage(Integer id, Model model){
        if (null!=id){
            Role role = roleService.selectByPrimaryKey(id);
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }

    //添加role
    @RequestMapping("role/save")
    @ResponseBody
    public ResultInfo saveRole(Role role){
        roleService.saveRole(role);
        return success("角色记录添加成功");
    }

    //修改role
    @RequestMapping("role/update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色记录更新成功");
    }

    //删除role
    @RequestMapping("role/delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("角色记录删除成功");
    }

    //跳转视图
    @RequestMapping("role/toAddGrantPage")
    public String toAddGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    //添加权限记录
    @RequestMapping("role/addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId){
        roleService.addGrant(mids,roleId);
        return success("权限添加成功");
    }

}
