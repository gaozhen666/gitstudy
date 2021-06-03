package com.gao.crm.controller;

import com.gao.crm.base.BaseController;
import com.gao.crm.service.PermissionService;
import com.gao.crm.service.UserService;
import com.gao.crm.utils.LoginUserUtil;
import com.gao.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;



    //系统登录页
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    //欢迎界面
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }

    //后台管理页面
    @RequestMapping("main")
    public String main(HttpServletRequest req){
        //通过工具类，从cookie中获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        User user = userService.selectByPrimaryKey(userId);
        //将用户对象设置到request作用域中
        req.setAttribute("user",user);
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
        req.getSession().setAttribute("permissions",permissions);
        return "main";
    }

}
