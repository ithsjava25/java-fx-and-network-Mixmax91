package com.example;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Represents an asynchronous connection interface for communicating with a server
 * This interface is to be used between the application and the connection to the client
 */

public interface NtfyConnection {

    /**
     *
     * @param filePath path to file being sent to client
     * @param fileType file type of the file as a string
     * @param topic current topic/chatroom will be added after client root adress
     * @return CompletableFuture for asynchronous calls and tests
     */
    CompletableFuture<Void> sendAttachment(Path filePath, String fileType, String topic);

    /**
     *
     * @param message message to be sent to client
     * @param topic current topic/chatroom will be added after client root adress
     * @return CompletableFuture for asynchronous calls and tests
     */
    CompletableFuture<Void> send(String message, String topic);

    /**
     *
     * @param messageHandler a consumer used with incoming NtfyMessageDto objects
     * @param topic current topic/chatroom will be added after client root adress
     * @return CompletableFuture for asynchronous calls and tests
     */
    CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler, String topic);

}
