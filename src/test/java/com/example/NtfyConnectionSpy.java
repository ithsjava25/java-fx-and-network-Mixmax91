package com.example;

import java.nio.file.Path;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;

    @Override
    public boolean sendAttachment(Path filePath, String fileType) {
        return true;
    }

    @Override
    public boolean send(String message) {
        this.message = message;
        return true;
    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler) {

    }
}
