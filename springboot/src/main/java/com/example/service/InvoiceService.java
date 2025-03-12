package com.example.service;

import com.example.entity.Invoice;
import com.example.mapper.InvoiceMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统公告业务处理
 **/
@Service
public class InvoiceService {

    @Resource
    private InvoiceMapper invoiceMapper;

    /**
     * 新增
     */
    public void add(Invoice invoice) {
        invoiceMapper.insert(invoice);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        invoiceMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void updateById(Invoice invoice) {
        invoiceMapper.updateById(invoice);
    }

    /**
     * 根据ID查询
     */
    public Invoice selectById(Integer id) {
        return invoiceMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Invoice> selectAll(Invoice invoice) {
        return invoiceMapper.selectAll(invoice);
    }

    /**
     * 分页查询
     */
    public PageInfo<Invoice> selectPage(Invoice invoice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Invoice> list = invoiceMapper.selectAll(invoice);
        return PageInfo.of(list);
    }
}