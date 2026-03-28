package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultProtocolValidatorTest {

    private DefaultProtocolValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DefaultProtocolValidator(5);
    }

    @Test
    void validate_shouldAcceptValidAuthRequest() {
        AuthRequest request = new AuthRequest(
                ProtocolTypes.REQUEST_AUTH,
                "req-1",
                "alice",
                "alicepwd"
        );

        assertThatCode(() -> validator.validate(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_shouldRejectAuthRequestWithWrongType() {
        AuthRequest request = new AuthRequest(
                "SEND",
                "req-1",
                "alice",
                "alicepwd"
        );

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(ProtocolException.class)
                .hasMessageContaining("Invalid AUTH.type value");
    }

    @Test
    void validate_shouldAcceptValidSendRequest() {
        SendMessageRequest request = new SendMessageRequest(
                ProtocolTypes.REQUEST_SEND,
                "req-2",
                "bob",
                "hello",
                "client-1"
        );

        assertThatCode(() -> validator.validate(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_shouldRejectBlankClientMsgId() {
        SendMessageRequest request = new SendMessageRequest(
                ProtocolTypes.REQUEST_SEND,
                "req-2",
                "bob",
                "hello",
                "   "
        );

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(ProtocolException.class)
                .hasMessageContaining("SEND.clientMsgId");
    }

    @Test
    void validate_shouldRejectTooLongText() {
        SendMessageRequest request = new SendMessageRequest(
                ProtocolTypes.REQUEST_SEND,
                "req-3",
                "bob",
                "abcdef",
                "client-2"
        );

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(ProtocolException.class)
                .hasMessageContaining("maxMessageLength");
    }
}
