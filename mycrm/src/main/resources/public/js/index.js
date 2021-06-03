layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    
    //用户登录，表单提交
    form.on('submit(login)',function (data) {
        //获取表单元素（用户名+密码）
        var fieldData = data.field;
        //判断参数是否为空
        if(fieldData.username=="undefined" || fieldData.username.trim()==""){
            layer.msg("用户名不能为空!");
            return false;
        }
        if (fieldData.password=="undefined" || fieldData.password.trim()==""){
            layer.msg("密码不能为空!");
            return false;
        }
        //发送ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{userName:fieldData.username,userPwd:fieldData.password},
            dataType:"json",
            success:function (data) {
                //判断是否成功
                if (data.code==200){
                    layer.msg("登录成功!",function () {
                        //将用户信息存到cookie中
                        var result = data.result;
                        $.cookie("userIdStr",result.userIdStr);
                        $.cookie("userName",result.userName);
                        $.cookie("trueName",result.trueName);

                        if ($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr",result.userIdStr,{expires:7});
                            $.cookie("userName",result.userName,{expires:7});
                            $.cookie("trueName",result.trueName,{expires:7});
                        }

                        //登录成功后跳转页面
                        window.location.href = ctx+"/main";
                    });
                } else {
                  //提示信息
                  layer.msg(data.msg);
                }
            }
        });
        //阻止表单跳转
        return false;
    })
});