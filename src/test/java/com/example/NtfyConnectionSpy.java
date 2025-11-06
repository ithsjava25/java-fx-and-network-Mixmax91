package com.example;

import java.nio.file.Path;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    Path filePath;
    String fileType;

    @Override
    public boolean sendAttachment(Path filePath, String fileType, String topic) {
        this.filePath = filePath;
        this.fileType = fileType;
        return true;
    }

    @Override
    public boolean send(String message, String topic) {
        this.message = message;
        return true;
    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler, String topic) {

    }
}
