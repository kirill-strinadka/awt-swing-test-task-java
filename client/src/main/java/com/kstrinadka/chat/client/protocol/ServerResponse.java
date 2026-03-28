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
        @JsonSubTypes.Type(value = AuthOkResponse.class, name = "AUTH_OK"),
        @JsonSubTypes.Type(value = AuthErrorResponse.class, name = "AUTH_ERROR"),
        @JsonSubTypes.Type(value = AckResponse.class, name = "ACK"),
        @JsonSubTypes.Type(value = IncomingResponse.class, name = "INCOMING"),
        @JsonSubTypes.Type(value = ErrorResponse.class, name = "ERROR")
})
public interface ServerResponse {

    ProtocolMessageType type();

    String requestId();
}
