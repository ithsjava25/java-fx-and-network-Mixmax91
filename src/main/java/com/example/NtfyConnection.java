package com.example;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface NtfyConnection {

    public boolean sendAttachment(Path filePath, String fileType, String topic);

    public boolean send(String message, String topic);

    public void receive(Consumer<NtfyMessageDto> messageHandler, String topic);

}
