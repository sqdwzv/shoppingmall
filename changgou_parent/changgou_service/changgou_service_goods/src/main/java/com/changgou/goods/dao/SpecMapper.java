package com.changgou.goods.dao;

import com.changgou.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {
    /**
     * 根据商品分类名查询规格
     */
    @Select("SELECT NAME,OPTIONS FROM tb_spec WHERE template_id IN(SELECT template_id FROM tb_category WHERE NAME =#{name})")
    List<Map> findListByCategoryName(@Param("name")String categoryName);

}
