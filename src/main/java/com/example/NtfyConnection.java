package com.example;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

    public CompletableFuture<Void> sendAttachment(Path filePath, String fileType, String topic);

    public CompletableFuture<Void> send(String message, String topic);

    public CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler, String topic);

}
