package com.example.demo.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQListener {

    @RabbitListener(queues = "sample.queue") // 메소드에게 메시지가 push 된다
    public void recieveMessage(final Message message){
        // queue가 엄청 늘어나나? 그건 아닌가 그냥 쌓아만 두는거라서.. subsc한상태에서는 어떻게 되는지 모르겠네...
        log.info(message.toString());
    }
}
