package com.gao.crm.service;

import com.gao.crm.base.BaseService;
import com.gao.crm.dao.CusDevPlanMapper;
import com.gao.crm.dao.SaleChanceMapper;
import com.gao.crm.query.CusDevPlanQuery;
import com.gao.crm.utils.AssertUtil;
import com.gao.crm.vo.CusDevPlan;
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
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    //多条件查询计划项列表
    public Map<String,Object> queryCusDevPlansByParams(CusDevPlanQuery cusDevPlanQuery){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(cusDevPlanQuery.getPage(),cusDevPlanQuery.getLimit());
        List<CusDevPlan> clist = cusDevPlanMapper.selectByParams(cusDevPlanQuery);
        PageInfo<CusDevPlan> plist = new PageInfo<>(clist);
        map.put("code",0);
        map.put("msg","");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }

    /**
     * 添加计划项
     *  1. 参数校验
     *      营销机会ID  非空  记录必须存在
     *      计划项内容   非空
     *      计划项时间   非空
     *  2. 设置参数默认值
     *      is_valid
     *      crateDate
     *      updateDate
     *  3. 执行添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCusDevPlan(CusDevPlan cusDevPlan){
        //参数校验
        checkParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
        //设置参数的默认值
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        //判断结果
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan)<1,"计划项记录添加失败");
    }

    private void checkParams(Integer saleChanceId, String planItem, Date planDate) {
        AssertUtil.isTrue(saleChanceId==null || saleChanceMapper.selectByPrimaryKey(saleChanceId)==null,"请设置营销机会id");
        AssertUtil.isTrue(StringUtils.isBlank(planItem),"请输入计划内容");
        AssertUtil.isTrue(planDate==null,"请输入计划项日期");
    }

    /**
     * 更新计划项
     *  1.参数校验
     *      id  非空 记录存在
     *      营销机会id 非空 记录必须存在
     *      计划项内容  非空
     *      计划项时间 非空
     *  2.参数默认值设置
     *      updateDate
     *  3.执行更新  判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        AssertUtil.isTrue(cusDevPlan.getId()==null || cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null,"待更新的记录不存在");
        //参数校验
        checkParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
        //设置默认值
        cusDevPlan.setUpdateDate(new Date());
        //判断结果
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)<1,"计划项记录更新失败");
    }

    /**
     * 删除计划项
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delCusDevPlan(Integer id){
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(id==null || cusDevPlan==null,"待删除的记录不存在");
        cusDevPlan.setIsValid(0);
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)<1,"计划项记录删除失败");
    }
}
