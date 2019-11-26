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
public class GoodsDelListener {
    @Autowired
    private ESManagerService esManagerService;
    @RabbitListener(queues = RabbitMQConfig.SEARCH_DELETE_QUEUE)
    public void receiveMessage(String spuId){
        System.out.println("删除索引库监听类,接收到的消息是"+spuId);
        //根据spuId删除索引库数据
        esManagerService.delDataBySpuId(spuId);
    }

}
