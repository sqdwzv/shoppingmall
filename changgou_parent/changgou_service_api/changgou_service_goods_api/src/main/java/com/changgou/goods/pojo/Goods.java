package com.changgou.goods.pojo;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * 商品组合实体类
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    @Override
    public String toString() {
        return "Goods{" +
                "spu=" + spu +
                ", skuList=" + skuList +
                '}';
    }

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
