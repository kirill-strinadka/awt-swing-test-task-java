package com.kstrinadka.chat.client.protocol;

public interface ProtocolCodec {

    String encode(ClientRequest request);

    ServerResponse decode(String line);
}
