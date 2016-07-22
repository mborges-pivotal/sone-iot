package io.pivotal.particle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import io.pivotal.particle.sse.SseController;

@Component
public class ParticleEventReceiver {
	
	private static Logger logger = LoggerFactory.getLogger(ParticleEventReceiver.class);
	
	@Autowired
	private SseController service;

	@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "events", durable = "true"),
            exchange = @Exchange(value = "amq.topic", durable = "true"), key = "events" ) )
    public void receiveMessage( Message<?> message )
    {
        String name = (String) message.getHeaders().get("eventName");
        String data = (String) message.getPayload();
        logger.info("{} - {}", name, data);
        
        service.sendParticleEvent(name, data);
    }

}
