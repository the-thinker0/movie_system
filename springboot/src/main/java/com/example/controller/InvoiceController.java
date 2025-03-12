package com.example.controller;

import com.example.common.Result;
import com.example.entity.Invoice;
import com.example.service.InvoiceService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 前端操作接口
 **/
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Resource
    private InvoiceService invoiceService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Invoice invoice) {
        invoiceService.add(invoice);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        invoiceService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Invoice invoice) {
        invoiceService.updateById(invoice);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Invoice invoice = invoiceService.selectById(id);
        return Result.success(invoice);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Invoice invoice) {
        List<Invoice> list = invoiceService.selectAll(invoice);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Invoice invoice,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Invoice> page = invoiceService.selectPage(invoice, pageNum, pageSize);
        return Result.success(page);
    }

}