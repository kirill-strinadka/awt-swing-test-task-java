package com.kstrinadka.chat.client.protocol;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthRequest.class, name = "AUTH"),
        @JsonSubTypes.Type(value = SendRequest.class, name = "SEND"),
        @JsonSubTypes.Type(value = PingRequest.class, name = "PING")
})
public interface ClientRequest {

    ProtocolMessageType type();

    String requestId();
}
