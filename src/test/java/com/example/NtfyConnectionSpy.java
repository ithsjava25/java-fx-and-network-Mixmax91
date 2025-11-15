package com.example;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    Path filePath;
    String fileType;
    String topic;

    @Override
    public CompletableFuture<Void> sendAttachment(Path filePath, String fileType, String topic) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.topic = topic;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> send(String message, String topic) {
        this.message = message;
        this.topic = topic;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler, String topic) {
        this.topic = topic;
        return CompletableFuture.completedFuture(null);
    }
}
