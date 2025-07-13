package org.example.authserver.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.management.Query;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange rollbackExchange() {
        return new TopicExchange("folder.rollback.exchange");
    }

    @Bean
    public Queue userMetadataRollbackQueue() {
        return new Queue("folder.rollback.user-metadata.queue");
    }

    @Bean
    public Binding metadataRollbackBinding() {
        return BindingBuilder.bind(userMetadataRollbackQueue())
                .to(rollbackExchange())
                .with("folder.rollback.*");
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
