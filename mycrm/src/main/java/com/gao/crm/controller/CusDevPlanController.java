package com.gao.crm.controller;

import com.gao.crm.base.BaseController;
import com.gao.crm.base.ResultInfo;
import com.gao.crm.query.CusDevPlanQuery;
import com.gao.crm.service.CusDevPlanService;
import com.gao.crm.service.SaleChanceService;
import com.gao.crm.vo.CusDevPlan;
import com.gao.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;
    /**
     * 客户开发主页面
     * @return
     */
    @RequestMapping("cus_dev_plan/index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }

    //进入开发计划项数据页面
    @RequestMapping("cus_dev_plan/toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Model model,Integer sid){
        //通过id查询营销机会数据
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sid);
        //将数据存到作用域中
        model.addAttribute("saleChance",saleChance);
        return "cusDevPlan/cus_dev_plan_data";
    }

    //查询营销机会的计划项数据列表
    @RequestMapping("cus_dev_plan/list")
    @ResponseBody
    public Map<String,Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery){
        return cusDevPlanService.queryCusDevPlansByParams(cusDevPlanQuery);
    }

    // 添加计划项
    @RequestMapping("cus_dev_plan/save")
    @ResponseBody
    public ResultInfo saveCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.saveCusDevPlan(cusDevPlan);
        return success("计划项添加成功");
    }

    //更新计划项
    @RequestMapping("cus_dev_plan/update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功");
    }

    //转发跳转到计划数据项页面
    @RequestMapping("cus_dev_plan/addOrUpdateCusDevPlanPage")
    public String addOrUpdateCusDevPlanPage(Integer sid,Integer id,Model model){
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        model.addAttribute("cusDevPlan",cusDevPlan);
        model.addAttribute("sid",sid);
        return "cusDevPlan/add_update";
    }

    /**
     * 删除计划项
     * @param id
     * @return
     */
    @RequestMapping("cus_dev_plan/delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.delCusDevPlan(id);
        return success("计划项删除成功");
    }

}
