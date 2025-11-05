package com.example;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface NtfyConnection {

    public boolean sendAttachment(Path filePath, String fileType);

    public boolean send(String message);

    public void receive(Consumer<NtfyMessageDto> messageHandler);

}
