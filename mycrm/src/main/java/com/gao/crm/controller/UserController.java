package com.gao.crm.controller;

import com.gao.crm.base.BaseController;
import com.gao.crm.base.ResultInfo;
import com.gao.crm.model.UserModel;
import com.gao.crm.query.UserQuery;
import com.gao.crm.service.UserService;
import com.gao.crm.utils.LoginUserUtil;
import com.gao.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    @PostMapping("user/login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd){
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
    }

    //视图转发方法
    @RequestMapping("user/toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    @PostMapping("user/updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest req,String oldPassword,String newPassword,String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        userService.updateUserPassword(userId,oldPassword,newPassword,confirmPassword);
        return resultInfo;
    }

    //转个人信息页面
    @RequestMapping("user/toSettingPage")
    public String toSettingPage(){
        return "user/setting";
    }

    @RequestMapping("user/queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

    //多条件查询用户数据
    @RequestMapping("user/list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        Map<String, Object> map = userService.queryUserByParams(userQuery);
        return map;
    }

    //进入用户页面
    @RequestMapping("user/index")
    public String index(){
        return "user/user";
    }

    //添加用户
    @RequestMapping("user/save")
    @ResponseBody
    public ResultInfo saveUser(User user){
        userService.saveUser(user);
        return success("用户添加成功");
    }

    //更新用户
    @RequestMapping("user/update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户更新成功");
    }

    //进入用户添加或更新页面
    @RequestMapping("user/addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model){
        if (null!=id){
            User user = userService.selectByPrimaryKey(id);
            model.addAttribute("user",user);
        }
        return "user/add_update";
    }

    //删除用户
    @RequestMapping("user/delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUserByIds(ids);
        return success("用户记录删除成功");
    }

}
