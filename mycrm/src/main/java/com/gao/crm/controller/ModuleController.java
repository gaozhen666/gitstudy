package com.gao.crm.controller;

import com.gao.crm.base.BaseController;
import com.gao.crm.base.ResultInfo;
import com.gao.crm.dto.TreeDto;
import com.gao.crm.service.ModuleService;
import com.gao.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

//    @RequestMapping("module/queryAllModules")
//    @ResponseBody
//    public List<TreeDto> queryAllModules(){
//        return moduleService.queryAllModules();
//    }

    @RequestMapping("module/queryAllModules")
    @ResponseBody
    public List<TreeDto> queryAllModules(Integer roleId){
        return moduleService.queryAllModules02(roleId);
    }

    @RequestMapping("module/index")
    public String index(){
        return "module/module";
    }

    @RequestMapping("module/list")
    @ResponseBody
    public Map<String,Object> moduleList(){
        return moduleService.moduleList();
    }

    // 添加资源页视图转发
    @RequestMapping("module/addModulePage")
    public String addModulePage(Integer grade, Integer parentId, Model model){
        model.addAttribute("grade",grade);
        model.addAttribute("parentId",parentId);
        return "module/add";
    }

    // 更新资源页视图转发
    @RequestMapping("module/updateModulePage")
    public String updateModulePage(Integer id,Model model){
        Module module = moduleService.selectByPrimaryKey(id);
        model.addAttribute("module",module);
        return "module/update";
    }

    //菜单添加
    @RequestMapping("module/save")
    @ResponseBody
    public ResultInfo saveModule(Module module){
        moduleService.saveModule(module);
        return success("菜单添加成功");
    }

    //根据等级查询菜单
    @RequestMapping("module/queryAllModulesByGrade")
    @ResponseBody
    public List<Map<String,Object>> queryAllModulesByGrade(Integer grade){
        return moduleService.queryAllModulesByGrade(grade);
    }

    //更新菜单
    @RequestMapping("module/update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("菜单更新成功");
    }

    //删除菜单
    @RequestMapping("module/delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModuleById(id);
        return success("菜单删除成功");
    }

}
