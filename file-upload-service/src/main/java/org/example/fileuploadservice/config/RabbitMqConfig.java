package org.example.fileuploadservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue s3ObjectQueue() {
        return new Queue("s3-object-queue", true);
    }

    @Bean
    public Queue folderDeleteQueue() {
        return new Queue("folder-content-delete-queue", true);
    }

    @Bean
    public Exchange exchange(){
        return new DirectExchange("s3-exchange", true, false);
    }

    @Bean Exchange folderDeleteExchange() {
        return new DirectExchange("folder.delete.exchange", true, false);
    }

    @Bean
    public Binding binding(Queue s3ObjectQueue, Exchange exchange) {
        return BindingBuilder.bind(s3ObjectQueue)
                .to(exchange)
                .with("s3-upload-key")
                .noargs();
    }

    @Bean
    public Binding folderDeleteBinding(Queue folderDeleteQueue, Exchange folderDeleteExchange) {
        return BindingBuilder.bind(folderDeleteQueue)
                .to(folderDeleteExchange)
                .with("folder.delete.key")
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
