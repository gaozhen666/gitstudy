package com.gao.crm.controller;

import com.gao.crm.annotations.RequiredPermission;
import com.gao.crm.base.BaseController;
import com.gao.crm.base.ResultInfo;
import com.gao.crm.query.SaleChanceQuery;
import com.gao.crm.service.SaleChanceService;
import com.gao.crm.service.UserService;
import com.gao.crm.utils.LoginUserUtil;
import com.gao.crm.vo.SaleChance;
import com.gao.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private UserService userService;

    //多条件分页查询
    @RequestMapping("sale_chance/list")
    @ResponseBody
    @RequiredPermission(code = "101001")
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest req){
        //查询参数 flag=1 代表当前查询为开发计划数据，设置查询分配人参数
        if (null!=flag && flag==1){
            //获取当前登录用户的id
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
            saleChanceQuery.setAssignMan(userId);
        }
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        return map;
    }

    //进入营销机会页面
    @RequestMapping("sale_chance/index")
    public String index(){
        return "saleChance/sale_chance";
    }

    //添加营销数据
    @RequestMapping("sale_chance/save")
    @ResponseBody
    @RequiredPermission(code = "101002")
    public ResultInfo saveSaleChance(HttpServletRequest req, SaleChance saleChance){
        //获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        User user = userService.selectByPrimaryKey(userId);
        saleChance.setCreateMan(user.getTrueName());
        saleChanceService.saveSaleChance(saleChance);
        return success("营销机会数据添加成功!");
    }

    //机会数据添加与更新页面视图转发
    @RequestMapping("sale_chance/addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        // 如果id不为空，表示是修改操作，修改操作需要查询被修改的数据
        if (null!=id){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //将数据存到作用域中
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    //更新营销机会数据
    @RequestMapping("sale_chance/update")
    @ResponseBody
    public ResultInfo updateSaleChance(HttpServletRequest req,SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功");
    }

    /**
     * 删除营销机会数据
     * @param ids
     * @return
     */
    @RequestMapping("sale_chance/delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        // 删除营销机会的数据
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会删除成功");
    }

    //更新营销机会的开发状态
    @RequestMapping("sale_chance/updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功");
    }

}
