package com.kstrinadka.chat.server.protocol;

public interface ProtocolValidator {

    void validate(ClientRequest request) throws ProtocolException;
}
