layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();

    $(".login-out").click(function () {
        //删除cookie
        $.removeCookie("userIdStr",{domain:"localhost",path:"/mycrm"});
        $.removeCookie("userName",{domain:"localhost",path:"/mycrm"});
        $.removeCookie("trueName",{domain:"localhost",path:"/mycrm"});
        window.location.href = ctx+"/index";
    });

});