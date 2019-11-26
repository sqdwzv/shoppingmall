package com.changgou.business.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
public class AdListener {
    /**
     * 监听方法
     * @param message
     */
    @RabbitListener(queues = "ad_update_queue")
    public void receiveMessage(String message){
        System.out.println("接收到的消息是"+message);
        //发起远程调用OKhttp
        OkHttpClient okHttpClient = new OkHttpClient();
        //设置访问路径
        String url = "http://192.168.200.128/ad_update?position="+message;
        Request request = new Request.Builder().url(url).build();
        //设置新的访问
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功
                System.out.println("请求成功"+response.message());
            }
        });
    }
}
