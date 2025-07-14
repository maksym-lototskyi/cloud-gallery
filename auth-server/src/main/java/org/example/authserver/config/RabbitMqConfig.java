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
    public TopicExchange creationExchange() {
        return new TopicExchange("folder.creation.exchange");
    }

    @Bean
    public Queue userMetadataRollbackQueue() {
        return new Queue("folder.rollback.user-metadata.queue");
    }

    @Bean
    public Queue metadataSuccessQueue() {
        return new Queue("folder.create.update-user-metadata.queue");
    }

    @Bean
    public Binding metadataRollbackBinding() {
        return BindingBuilder.bind(userMetadataRollbackQueue())
                .to(rollbackExchange())
                .with("folder.rollback.*");
    }

    @Bean
    public Binding metadataSuccessBinding() {
        return BindingBuilder.bind(metadataSuccessQueue())
                .to(creationExchange())
                .with("folder.create.success.user-metadata");
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
