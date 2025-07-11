package org.example.fileuploadservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue photoQueue() {
        return new Queue("photo-queue", true);
    }

    @Bean
    public Exchange exchange(){
        return new DirectExchange("photo-exchange", true, false);
    }

    @Bean
    public Binding binding(Queue photoQueue, Exchange exchange) {
        return BindingBuilder.bind(photoQueue)
                .to(exchange)
                .with("photo-upload-key")
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
