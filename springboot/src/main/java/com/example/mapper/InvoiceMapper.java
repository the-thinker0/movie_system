package com.example.mapper;

import com.example.entity.Invoice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作invoice相关数据接口
 */
public interface InvoiceMapper {

    /**
     * 新增
     */
    int insert(Invoice invoice);

    /**
     * 删除
     */
    @Delete("delete from invoice where id = #{id}")
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Invoice invoice);

    /**
     * 根据ID查询
     */
    @Select("select * from invoice where id = #{id}")
    Invoice selectById(Integer id);

    /**
     * 查询所有
     */
    List<Invoice> selectAll(Invoice invoice);
//
//    @Select("select * from invoice where username = #{username}")
//    Invoice selectByUsername(String username);

}