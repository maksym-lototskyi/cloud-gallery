package org.example.photoservice.config;

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
    public Queue photoUploadedQueue(){
        return new Queue("photo-uploaded-queue", true);
    }

    @Bean
    public Queue photoUploadFailedQueue(){
        return new Queue("photo-upload-failed-queue", true);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange("photo-exchange", true, false);
    }

    @Bean
    public Binding bindingPhotoUploaded(Queue photoUploadedQueue, Exchange exchange) {
        return BindingBuilder.bind(photoUploadedQueue)
                .to(exchange)
                .with("photo-uploaded-key")
                .noargs();
    }

    @Bean
    public Binding bindingPhotoUploadFailed(Queue photoUploadFailedQueue, Exchange exchange) {
        return BindingBuilder.bind(photoUploadFailedQueue)
                .to(exchange)
                .with("photo-upload-failed-key")
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
