package com.kstrinadka.chat.server.protocol;

public interface ProtocolMessageCodec {

    ClientRequest decodeRequest(String rawMessage) throws ProtocolException;

    String encodeResponse(ServerResponse response) throws ProtocolException;
}
