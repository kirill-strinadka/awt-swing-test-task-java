package com.kstrinadka.chat.client.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class ConnectionToServer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ConnectionToServer.class);

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private ConnectionToServer(Socket socket, BufferedReader reader, BufferedWriter writer) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
    }

    public static ConnectionToServer open(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
        );
        return new ConnectionToServer(socket, reader, writer);
    }

//    public boolean isConnected() {
//        return socket.isConnected() && writer != null && reader != null;
//    }
//
//    public boolean isOpen() {
//        return !socket.isClosed();
//    }

    public void writeLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() {
        closeQuietly(reader);
        closeQuietly(writer);
        closeQuietly(socket);
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Exception ex) {
            log.debug("Failed to close resource cleanly", ex);
        }
    }
}
