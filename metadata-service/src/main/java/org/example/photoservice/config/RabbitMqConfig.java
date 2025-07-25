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
        return new Queue("s3-uploaded-queue", true);
    }

    @Bean
    public Queue photoUploadFailedQueue(){
        return new Queue("s3-upload-failed-queue", true);
    }
    @Bean Queue folderDeleteRollbackQueue() {
        return new Queue("folder-delete-rollback-queue", true);
    }
    @Bean
    public Exchange exchange() {
        return new DirectExchange("s3-upload-exchange", true, false);
    }

    @Bean
    public Exchange folderDeleteRollbackExchange() {
        return new DirectExchange("folder.delete.rollback.exchange", true, false);
    }

    @Bean
    public TopicExchange creationExchange() {
        return new TopicExchange("folder.creation.exchange");
    }


    @Bean
    public Queue metadataCreateQueue() {
        return new Queue("folder.create.metadata.queue");
    }

    @Bean
    public Binding metadataCreateBinding(TopicExchange creationExchange) {
        return BindingBuilder.bind(metadataCreateQueue())
                .to(creationExchange)
                .with("folder.create.metadata");
    }

    @Bean
    public Binding folderDeleteRollbackBinding(Queue folderDeleteRollbackQueue, Exchange folderDeleteRollbackExchange) {
        return BindingBuilder.bind(folderDeleteRollbackQueue)
                .to(folderDeleteRollbackExchange)
                .with("folder.delete.rollback.key")
                .noargs();
    }

    @Bean
    public Binding bindingPhotoUploaded(Queue photoUploadedQueue, Exchange exchange) {
        return BindingBuilder.bind(photoUploadedQueue)
                .to(exchange)
                .with("upload-success-key")
                .noargs();
    }

    @Bean
    public Binding bindingPhotoUploadFailed(Queue photoUploadFailedQueue, Exchange exchange) {
        return BindingBuilder.bind(photoUploadFailedQueue)
                .to(exchange)
                .with("upload-failed-key")
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
