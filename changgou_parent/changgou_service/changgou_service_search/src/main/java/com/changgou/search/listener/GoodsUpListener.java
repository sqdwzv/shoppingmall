package com.changgou.search.listener;

import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbtMQ监听类
 */
@Component
public class GoodsUpListener {
    @Autowired
    private ESManagerService esManagerService;
    @RabbitListener(queues = RabbitMQConfig.SEARCH_ADD_QUEUE)
    public void receiveMessage(String spuId){
        System.out.println("接收到的消息是"+spuId);
        //查询skuList并导入索引库
        esManagerService.improtDataBySpuId(spuId);
    }
}
