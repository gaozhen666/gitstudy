package com.gao.crm.dao;

import com.gao.crm.base.BaseMapper;
import com.gao.crm.dto.TreeDto;
import com.gao.crm.vo.Module;

import java.util.List;
import java.util.Map;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    List<TreeDto> queryAllModules();

    List<Module> queryModules();

    Module queryModuleByGradeAndModuleName(Integer grade, String moduleName);

    Module queryModuleByGradeAndUrl(Integer grade, String url);

    Module queryModuleByOptValue(String optValue);

    List<Map<String, Object>> queryAllModulesByGrade(Integer grade);

    Integer countSubModuleByParentId(Integer parentId);
}