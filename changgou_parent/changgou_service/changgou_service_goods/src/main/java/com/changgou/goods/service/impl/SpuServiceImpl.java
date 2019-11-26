package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import feign.Param;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    //分布式id
    private IdWorker idWorker;

    @Autowired
    private SpuMapper spuMapper;
    //分类dao
    @Autowired
    private CategoryMapper categoryMapper;
    //品牌dao
    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加商品 SPU+SKU列表 goods商品组合类
     *
     * @param
     */
    @Override
    @Transactional
    public void add(Goods goods) {
        //1.添加spu
        Spu spu = goods.getSpu();
        long nextId = idWorker.nextId();
        spu.setId(String.valueOf(nextId));//设置分布式唯一id
        spu.setIsDelete("0");    //设置删除状态
        spu.setIsMarketable("0");//设置上架状态
        int i = spuMapper.insertSelective(spu);//添加spu
        System.out.println(i);
        //2.添加sku
        this.saveSkuList(goods);
    }
    //添加sku数据
    private void saveSkuList(Goods goods) {
        //获取spu
        Spu spu = goods.getSpu();
        //根据id获取分类信息
        Category category = (Category) categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //根据id获取品牌信息
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        /**
         * 添加品牌与分类之间的联系
         */
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(spu.getCategory3Id());
        categoryBrand.setBrandId(spu.getBrandId());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        System.out.println(count);
        /**
         * 判断是否这个品牌和分类的数据关系没有就添加
         */
        if (count == 0){
            //如果没有就添加品牌和分类之间的关系
            categoryBrandMapper.insertSelective(categoryBrand);
        }
        //获取sku集合
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null) {
            //循环填充数据并添加到集合中
            for (Sku sku : skuList) {
                //设置skuId
                sku.setId(String.valueOf(idWorker.nextId()));
                //设置sku规格数据 前段传过来的只需要判断是否为空
                if (StringUtils.isEmpty(sku.getSpec())) {
                    sku.setSpec("{}");
                }
                //设置sku名称(spu名称+规格)
                String name = spu.getName();
                //将规格的json转换为map,蒋map中的value进行名称拼接
                Map<String, String> map = JSON.parseObject(sku.getSpec(), Map.class);
                if (map != null && map.size() > 0) {
                    for (String value : map.values()) {
                        name +=" "+ value;
                    }
                }
                sku.setName(name);
                //设置spuId
                sku.setSpuId(spu.getId());
                //设置创建与修改时间
                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());
                //商品分类Id
                sku.setCategoryId(spu.getCategory3Id());
                //设置商品分类名称
                sku.setCategoryName(category.getName());
                //设置品牌名称
                sku.setBrandName(brand.getName());
                //将sku添加到数据库
                skuMapper.insertSelective(sku);

            }
        }
    }


    /**
     * 修改
     *
     * @param goods
     */
    @Override
    @Transactional
    public void update(Goods goods) {
        //1.先修改spu
        Spu spu = goods.getSpu();
        int i = spuMapper.updateByPrimaryKeySelective(spu);
        System.out.println("修改影响行数"+i);
        //2.删除原有sku
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spu.getId());
        skuMapper.deleteByExample(example);
        //3.添加sku
        this.saveSkuList(goods);
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //判断上下架状态
        if ("1".equals(spu.getIsMarketable())){
            throw new RuntimeException("必须先下架再删除");
        }
        //设置未审核
        spu.setStatus("0");
        //删除状态
        spu.setIsDelete("1");

        spuMapper.updateByPrimaryKeySelective(spu);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Spu>) spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Spu>) spuMapper.selectByExample(example);
    }

    /**
     * 根据ID查询商品
     * @param id
     * @return
     */
    @Override
    public Goods findGoosById(String id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //查询sku
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        List<Sku> skus = skuMapper.selectByExample(example);
        //封装返回
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skus);
        return goods;
    }

    /**
     * 审核商品
     * @param id
     */
    @Override
    public void audit(String id) {
        //1.判断商品是否存在
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            throw new RuntimeException("当前商品不存在");
        }
        //2.查看商品是否为删除状态
        if ("1".equals(spu.getIsDelete())){
            throw new RuntimeException("当前商品为删除状态");
        }
        //3.修改上架状态和审核状态
        spu.setIsMarketable("1");
        spu.setStatus("1");
        //4.执行修改操作
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 下架商品
     * @param id
     */
    @Override
    public void pull(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //查看商品
        if (spu == null){
            throw new RuntimeException("商品不存在");
        }
        //查看商品是否删除状态
        if ("1".equals(spu.getIsDelete())){
            throw new RuntimeException("商品为删除状态");
        }
        //如果为删除状态,则修改上下架状态为下架状态
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);

    }

    /**
     * 商品上架
     * @param id
     */
    @Override
    public void put(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //判断审核状态
        if (!"1".equals(spu.getStatus())){
            throw new RuntimeException("商品未审核");
        }
        //修改上架状态
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品恢复
     * @param id
     */
    @Override
    public void restore(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //检出是否为删除状态
        if (!"1".equals(spu.getIsDelete())){
            throw new RuntimeException("商品未删除");
        }
        //设置商品未删除未审核
        spu.setIsDelete("0");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 物理删除
     * @param id
     */
    @Override
    public void realDelete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //判断是否为删除状态
        if ("0".equals(spu.getIsDelete())){
            throw new RuntimeException("商品未删除");
        }
        //删除sku
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        spuMapper.deleteByExample(example);
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andEqualTo("sn", searchMap.get("sn"));
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andEqualTo("isMarketable", searchMap.get("isMarketable"));
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
