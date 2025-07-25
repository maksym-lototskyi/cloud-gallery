package org.example.authserver.listeners;

import org.example.authserver.exception.NotFoundException;
import org.example.authserver.model.User;
import org.example.authserver.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserMetadataListener {
    private final UserRepository userRepository;

    public UserMetadataListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "folder.rollback.user-metadata.queue")
    @Transactional
    public void handleUserMetadataRollback(UUID userUUID) {
        System.out.println("Received rollback request for user metadata with UUID: " + userUUID);
        userRepository.deleteByUserId(userUUID);
    }

    @RabbitListener(queues = "folder.create.update-user-metadata.queue")
    public void handleUserMetadataUpdate(UUID userUUID) {
        Optional<User> userOpt = userRepository.findByUserId(userUUID);
        if(userOpt.isEmpty()){
            return;
        }

        User user = userOpt.get();
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        System.out.println("Metadata is updated successfully");
    }
}
