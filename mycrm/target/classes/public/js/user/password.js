layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    
    //用户登录，表单提交
    form.on('submit(saveBtn)',function (data) {
        //获取表单元素（用户名+密码）
        var fieldData = data.field;
        //判断参数是否为空
        if(fieldData.old_password=="undefined" || fieldData.old_password.trim()==""){
            layer.msg("原始密码不能为空!");
            return false;
        }
        if (fieldData.new_password=="undefined" || fieldData.new_password.trim()==""){
            layer.msg("新密码不能为空!");
            return false;
        }
        if (fieldData.again_password=="undefined" || fieldData.again_password.trim()==""){
            layer.msg("确认密码不能为空!");
            return false;
        }
        //发送ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePassword",
            data:{oldPassword:fieldData.old_password,newPassword:fieldData.new_password,confirmPassword:fieldData.again_password},
            dataType:"json",
            success:function (data) {
                //判断是否成功
                if (data.code==200){
                    layer.msg("用户密码修改成功",function () {
                        //删除cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/mycrm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/mycrm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/mycrm"});
                        //登录成功后跳转页面
                        window.parent.location.href = ctx+"/index";
                    });
                } else {
                  //提示信息
                  layer.msg(data.msg);
                }
            }
        });
        //阻止跳转
        return false;
    })
});