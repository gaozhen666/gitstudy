package com.gao.crm.service;

import com.gao.crm.base.BaseService;
import com.gao.crm.dao.UserMapper;
import com.gao.crm.dao.UserRoleMapper;
import com.gao.crm.model.UserModel;
import com.gao.crm.query.UserQuery;
import com.gao.crm.utils.AssertUtil;
import com.gao.crm.utils.Md5Util;
import com.gao.crm.utils.PhoneUtil;
import com.gao.crm.utils.UserIDBase64;
import com.gao.crm.vo.User;
import com.gao.crm.vo.UserRole;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    //用户登录
    public UserModel userLogin(String userName,String userPwd){
        //验证输入的用户名和密码
        checkLoginParams(userName,userPwd);
        //根据用户名查询用户对象
        User user = userMapper.queryUserByUserName(userName);
        //判断用户是否存在
        AssertUtil.isTrue(user==null,"用户不存在或已注销");
        //校验密码
        checkLoginPwd(userPwd,user.getUserPwd());
        //返回用户相关信息
        return buildUserInfo(user);
    }

    //构建返回的用户信息
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        //设置用户信息，将UserId加密
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    //验证密码是否正确
    private void checkLoginPwd(String userPwd, String userPwd1) {
        //对密码加密(因为数据库中密码都是加密的)
        userPwd = Md5Util.encode(userPwd);
        //验证密码是否正确
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"用户密码不正确");
    }

    //验证登录参数
    private void checkLoginParams(String userName, String userPwd) {
        //判断用户名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //判断密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
    }

    /**
     * 用户密码修改
     * 1. 参数校验
     * 用户ID：userId 非空 用户对象必须存在
     * 原始密码：oldPassword 非空 与数据库中密文密码保持一致
     * 新密码：newPassword 非空 与原始密码不能相同
     * 确认密码：confirmPassword 非空 与新密码保持一致
     * 2. 设置用户新密码
     * 新密码进行加密处理
     * 3. 执行更新操作
     * 受影响的行数小于1，则表示修改失败
     *
     * 注：在对应的更新方法上，添加事务控制
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        //通过userId获取对象
        User user = userMapper.selectByPrimaryKey(userId);
        //参数校验
        checkPasswordParams(user,oldPassword,newPassword,confirmPassword);
        user.setUserPwd(Md5Util.encode(newPassword));
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"密码修改失败");
    }

    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        AssertUtil.isTrue(user==null,"用户未登录或不存在");
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码");
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码");
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不能与原密码相同");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"请输入确认密码");
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)),"新密码与确认密码不一致");
    }

    //查询所有的销售人员
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    //多条件分页查询用户数据
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        List<User> list = userMapper.selectByParams(userQuery);
        PageInfo<User> plist = new PageInfo<>(list);
        map.put("code",0);
        map.put("msg","");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }

    /**
     * 添加用户
     *  1. 参数校验
     *      用户名 非空 唯一性
     *      邮箱   非空
     *      手机号 非空  格式合法
     *  2. 设置默认参数
     *      isValid 1
     *      creteDate   当前时间
     *      updateDate  当前时间
     *      userPwd 123456 -> md5加密
     *  3. 执行添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user){
        //参数校验
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        User temp = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(temp!=null,"用户已存在");
        //设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        //判断添加结果
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"用户添加失败");
        relationUserRole(user.getId(),user.getRoleIds());
    }

    //定义校验参数的方法
    private void checkParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"电话格式不正确");
    }

    //更新用户
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"更新的记录不存在");

        User temp2 = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(temp2!=null && !temp.getUserName().equals(temp2.getUserName()),"用户已存在");

        //参数校验
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //设置默认参数
        user.setUpdateDate(new Date());
        //执行更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户更新失败");
        relationUserRole(temp.getId(),user.getRoleIds());
    }

    private void relationUserRole(Integer userId, String roleIds) {
        Integer count = userRoleMapper.countUserRoleByUserId(userId);

        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"用户角色分配失败");
        }

        if (StringUtils.isNotBlank(roleIds)){
            //重新添加新的元素
            List<UserRole> userRoles = new ArrayList<>();
            for(String s:roleIds.split(",")){
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles)<userRoles.size(),"用户角色分配失败");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(null==userId || null==user,"待删除的记录不存在");
        int count = userRoleMapper.countUserRoleByUserId(userId);
        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色删除失败");
        }
        user.setIsValid(0);
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户记录删除失败");
    }

    //删除用户
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids){
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择待删除的用户记录");
        AssertUtil.isTrue(userMapper.deleteBatch(ids)<1,"用户记录删除失败");
    }

}
