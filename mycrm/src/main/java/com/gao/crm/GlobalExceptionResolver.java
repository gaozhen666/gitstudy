package com.gao.crm;

import com.alibaba.fastjson.JSON;
import com.gao.crm.base.ResultInfo;
import com.gao.crm.exceptions.NoLoginException;
import com.gao.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * 方法返回值类型
     * 视图
     * JSON
     * 如何判断方法的返回类型：
     * 如果方法级别配置了 @ResponseBody 注解，表示方法返回的是JSON；
     * 反之，返回的是视图页面
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //如果是未登录异常，则先执行相关拦截操作
        if (ex instanceof NoLoginException){
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }
        //设置默认异常处理
        ModelAndView mv = new ModelAndView();
        mv.setViewName("error");
        mv.addObject("code",400);
        mv.addObject("msg","系统异常，请稍后再试");
        //判断HandlerMethod
        if (handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法上的ResponseBody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断responsebody注解是否存在
            if (null==responseBody){
                if (ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    mv.addObject("code",pe.getCode());
                    mv.addObject("msg",pe.getMsg());
                }
                return mv;
            }else {
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试");
                if (ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                //设置响应类型和编码格式（响应JSON格式）
                response.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    //将对象转为json格式
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                   if (out!=null){
                       out.close();
                   }
                }
                return null;
            }
        }
        return mv;
    }
}
