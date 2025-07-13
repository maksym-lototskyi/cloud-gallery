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
    public Queue fileuploadCreateQueue() {
        return new Queue("folder.create.file-upload.queue");
    }
    @Bean
    public TopicExchange creationExchange() {
        return new TopicExchange("folder.creation.exchange");
    }

    @Bean
    public Binding metadataCreateBinding() {
        return BindingBuilder.bind(fileuploadCreateQueue())
                .to(creationExchange())
                .with("folder.create.file-upload.key");
    }

    @Bean
    public Exchange exchange(){
        return new DirectExchange("s3-exchange", true, false);
    }

    @Bean
    public Binding binding(Queue s3ObjectQueue, Exchange exchange) {
        return BindingBuilder.bind(s3ObjectQueue)
                .to(exchange)
                .with("s3-upload-key")
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
