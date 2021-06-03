package com.gao.crm.interceptors;

import com.gao.crm.exceptions.NoLoginException;
import com.gao.crm.service.UserService;
import com.gao.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private UserService userService;

    /**
     * 判断用户是否是登录状态
     * 获取Cookie对象，解析用户ID的值
     * 如果用户ID不为空，且在数据库中存在对应的用户记录，表示请求合法
     * 否则，请求不合法，进行拦截，重定向到登录页面
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的用户id
        Integer id = LoginUserUtil.releaseUserIdFromCookie(request);
        //判断id是否为空，且数据库中存在对应的用户记录
        if (null==id || null==userService.selectByPrimaryKey(id)){
            //抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
