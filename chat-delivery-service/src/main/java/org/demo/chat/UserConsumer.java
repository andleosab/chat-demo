package org.demo.chat;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.websockets.next.OpenConnections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

    @Inject
    OpenConnections connections;

    @Incoming("user-created")
    public void userCreated(UserCreatedEvent event) {
        log.info("==> New user created: {}", event.username());
        UserCreatedMessage msg = new UserCreatedMessage(
            MessageType.USER_CREATED,
            event.userId(),
            event.username(),
            event.email(),
            event.timestamp()
        );
        connections.stream().forEach(conn ->
            conn.sendText(msg).subscribe().with(
                unused -> log.info("==> Notified {} of new user {}", conn.userData().get(WsKeys.USERNAME), event.username()),
                failure -> log.error("==> Failed to notify connection {}", conn.id(), failure)
            )
        );
    }
}
