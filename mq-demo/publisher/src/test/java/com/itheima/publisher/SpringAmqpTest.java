package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringAmqpTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testWorkQueue(){
        String queueName = "work.queue";
        String message;
        for (int i = 0; i < 50; i++) {
            message = "Hello SpringAMQP" + " " + (i + 1);
            rabbitTemplate.convertAndSend(queueName,message);
        }
    }

    @Test
    public void testFanoutQueue(){
        String exchangeName = "hmall.fanout";
        String message = "hello everyone";

        rabbitTemplate.convertAndSend(exchangeName,null,message);
    }

    @Test
    public void testDirectQueue(){
        String exchangeName = "hmall.direct";
        String message = "hello everyone this is blue";

        rabbitTemplate.convertAndSend(exchangeName,"blue",message);
    }
}