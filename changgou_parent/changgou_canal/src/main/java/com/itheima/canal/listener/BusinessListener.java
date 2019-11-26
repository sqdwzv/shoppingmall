package com.itheima.canal.listener;


import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author ZJ
 */
@CanalEventListener//声明当前类为canal的监听类
public class  BusinessListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ListenPoint(schema = "changgou_business", table = {"tb_ad"})
    //evenType操作数据库类型  rowData当前操作数据库的数据
    public void adUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("广告数据发生变化");
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        //修改前数据

        for(CanalEntry.Column column: rowData.getBeforeColumnsList()) {
            if(column.getName().equals("position")){
                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
                //发送消息 交换机 路由key(没有就是队列名称)
                rabbitTemplate.convertAndSend("","ad_update_queue",column.getValue());  //发送消息到mq
                break;
            }
        }


        //修改后数据
        for(CanalEntry.Column column: rowData.getAfterColumnsList()) {
            if(column.getName().equals("position")){
                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
                rabbitTemplate.convertAndSend("","ad_update_queue",column.getValue());  //发送消息到mq
                break;
            }
        }
        //rowData.getAfterColumnsList().forEach((c)-> System.out.println("改变之后的数据"+c.getName()+c.getValue()));
    }
}
