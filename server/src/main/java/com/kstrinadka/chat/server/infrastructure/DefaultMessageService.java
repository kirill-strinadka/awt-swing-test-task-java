package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.MessageIdGenerator;
import com.kstrinadka.chat.server.application.MessageService;
import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.application.UserRepository;
import com.kstrinadka.chat.server.domain.DeliveryResult;

import java.time.Clock;

public final class DefaultMessageService implements MessageService {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;
    private final MessageIdGenerator messageIdGenerator;
    private final Clock clock;

    public DefaultMessageService(
            UserRepository userRepository,
            SessionRegistry sessionRegistry,
            MessageIdGenerator messageIdGenerator,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.sessionRegistry = sessionRegistry;
        this.messageIdGenerator = messageIdGenerator;
        this.clock = clock;
    }

    @Override
    public DeliveryResult prepareMessage(String from, String to, String text, String clientMessageId) {
        throw new UnsupportedOperationException("not implemented");
    }
}
