package com.gao.crm.service;

import com.gao.crm.base.BaseService;
import com.gao.crm.dao.SaleChanceMapper;
import com.gao.crm.enums.DevResult;
import com.gao.crm.enums.StateStatus;
import com.gao.crm.query.SaleChanceQuery;
import com.gao.crm.utils.AssertUtil;
import com.gao.crm.utils.PhoneUtil;
import com.gao.crm.vo.SaleChance;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    //多条件分页查询
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        List<SaleChance> list = saleChanceMapper.selectByParams(saleChanceQuery);
        PageInfo<SaleChance> plist = new PageInfo<>(list);
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }

    /**
     * 营销机会数据添加
     *   1.参数校验
     *      customerName:非空
     *      linkMan:非空
     *      linkPhone:非空 11位手机号
     *   2.设置相关参数默认值
     *      state:默认未分配  如果选择分配人  state 为已分配
     *      assignTime:如果  如果选择分配人   时间为当前系统时间
     *      devResult:默认未开发 如果选择分配人devResult为开发中 0-未开发 1-开发中 2-开发成功 3-开发失败
     *      isValid:默认有效数据(1-有效  0-无效)
     *      createDate updateDate:默认当前系统时间
     *   3.执行添加 判断结果
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance){
        //参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //设置相关参数的默认值
        saleChance.setState(StateStatus.UNSTATE.getType());
        saleChance.setDevResult(DevResult.UNDEV.getStatus());
        //选择分配人
        if (StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setAssignTime(new Date());
        }
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());

        //执行添加
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)<1,"添加失败!");
    }

    private void checkParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入联系人电话");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"请输入正确的手机号");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        //通过id查询记录
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp==null,"待更新的记录不存在");
        //参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //设置相关参数的值
        saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            // 如果原始记录未分配，修改后改为已分配
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }else if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())){
            // 如果原始记录已分配，修改后改为未分配
            saleChance.setAssignMan("");
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setAssignTime(null);
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }

        //执行更新，判断结果
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"营销机会数据更新失败");
    }

    /**
     * 营销机会数据删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        // 判断要删除的id是否为空
        AssertUtil.isTrue(null==ids || ids.length==0,"请选择要删除的数据");
        //删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)<1,"营销机会数据删除失败");
    }

    /**
     * 更新营销机会的状态
     *      成功 = 2
     *      失败 = 3
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id,Integer devResult){
        AssertUtil.isTrue(id==null,"待更新的记录不存在");
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(saleChance==null,"待更新的记录不存在");
        saleChance.setDevResult(devResult);
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"机会数据更新失败");
    }
}
