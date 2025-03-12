package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.entity.GoodsStock;
import com.example.entity.Orders;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类业务处理
 **/

// @Service注解 标记了该类为 Spring 的服务类，使其可以被 Spring 容器管理
@Service
public class OrdersService {
    // 通过 @Resource 注解注入了 OrdersMapper 接口，用于调用数据库操作方法
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private GoodsStockService goodsStockService;

    /**
     * 添加新的分类记录
     */
    // 事务注解
    @Transactional
    public void add(Orders orders) {
        orders.setOrderNo(IdUtil.fastSimpleUUID());     // 唯一的订单编号
        orders.setTime(DateUtil.now());
        orders.setStatus("待支付");
        // 扣减库存
        GoodsStock goodsStock=goodsStockService.selectById(orders.getGoodsId());
        if(goodsStock==null){
            throw new CustomException("电影不存在");
        }

        // 出货后，扣除库存
        int store=goodsStock.getTickets()-orders.getNum();
        if(store<0){
            throw new CustomException("电影票库存不足");
        }
        // 库存充足时，更新库存容量
        goodsStock.setTickets(store);
        goodsStockService.updateById(goodsStock);
        // 调用 ordersMapper 接口中的 insert 方法，将当前的记录插入数据库
        ordersMapper.insert(orders);
    }

    // 余下的方法可以直接从接口的实例 ordersMapper 中进行调用
    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        // 如果用户已经取消订单，需要返还现有的库存
        if("已取消".equals(orders.getStatus())){
            Integer goodsId=orders.getGoodsId();
            GoodsStock goods=goodsStockService.selectById(goodsId);
            if(goods!=null){
                goods.setTickets(goods.getTickets()+orders.getNum());
                goodsStockService.updateById(goods);
            }
        }
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     * 这一部分的代码稍有不同，因为需要指定分页相关的代码逻辑
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        // 使用 PageHelper.startPage 方法，设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        // 调用 ordersMapper.selectAll 方法，从数据库中查询分页之后的农产品分类记录
        List<Orders> list = ordersMapper.selectAll(orders);
        // 将查询结果封装为 PageInfo 对象并返回
        return PageInfo.of(list);
    }

}