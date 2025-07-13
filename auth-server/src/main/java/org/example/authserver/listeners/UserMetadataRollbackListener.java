package org.example.authserver.listeners;

import org.example.authserver.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UserMetadataRollbackListener {
    private final UserRepository userRepository;

    public UserMetadataRollbackListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "folder.rollback.user-metadata.queue")
    @Transactional
    public void handleUserMetadataRollback(UUID userUUID) {
        System.out.println("Received rollback request for user metadata with UUID: " + userUUID);
        userRepository.deleteByUserId(userUUID);
    }
}
