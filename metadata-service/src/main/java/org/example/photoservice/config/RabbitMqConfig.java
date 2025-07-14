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
    @Bean
    public Exchange exchange() {
        return new DirectExchange("s3-upload-exchange", true, false);
    }

    @Bean
    public TopicExchange creationExchange() {
        return new TopicExchange("folder.creation.exchange");
    }

    @Bean
    public TopicExchange rollbackExchange() {
        return new TopicExchange("folder.rollback.exchange");
    }

    @Bean
    public Queue metadataCreateQueue() {
        return new Queue("folder.create.metadata.queue");
    }

    @Bean
    public Queue metadataRollbackQueue() {
        return new Queue("folder.rollback.metadata.queue");
    }

    @Bean
    public Queue metadataSuccessQueue() {
        return new Queue("folder.create.update-folder-metadata.queue");
    }

    @Bean
    public Binding metadataCreateBinding(TopicExchange creationExchange) {
        return BindingBuilder.bind(metadataCreateQueue())
                .to(creationExchange)
                .with("folder.create.metadata");
    }

    @Bean
    public Binding metadataRollbackBinding(TopicExchange rollbackExchange) {
        return BindingBuilder.bind(metadataRollbackQueue())
                .to(rollbackExchange)
                .with("folder.rollback.metadata");
    }

    @Bean
    public Binding metadataSuccessBinding(TopicExchange creationExchange) {
        return BindingBuilder.bind(metadataSuccessQueue())
                .to(creationExchange)
                .with("folder.create.success.metadata");
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
