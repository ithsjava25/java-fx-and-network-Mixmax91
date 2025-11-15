package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Implementation of NtfyConnection used to connect with http client
 * Contains overloaded constructors for testing
 */

public class NtfyConnector implements NtfyConnection{
    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor loading hostname from .env
     */
    public NtfyConnector(){
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    /**
     * creates a new connection with specified hostname
     * @param hostName
     */
    public NtfyConnector(String hostName){
        this.hostName = hostName;
    }

    /**
     * Sends a file to client
     * @param filePath path to file being sent to client
     * @param fileType file type of the file as a string
     * @param topic current topic/chatroom
     * @return
     */
    @Override
    public CompletableFuture<Void> sendAttachment(Path filePath, String fileType, String topic) {


        try {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofFile(filePath))
                .uri(URI.create(hostName + "/" + topic))
                .header("Filename", filePath.getFileName().toString())
                .build();

            return http.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> { /* nothing */})
                    .exceptionally(throwable -> {
                        System.out.println("Error sending message");
                        return null;
                    });
        } catch (Exception ex) {
            System.out.println("Error sending message");
            CompletableFuture<Void> failure = new CompletableFuture<>();
            failure.completeExceptionally(ex);
            return failure;
        }
    }

    /**
     *
     * @param message message to be sent to client
     * @param topic current topic/chatroom will be added after client root adress
     * @return
     */
    @Override
    public CompletableFuture<Void> send(String message, String topic) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/" + topic))
                .build();

        return http.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> { /* nothing */})
                .exceptionally(throwable -> {
                    System.out.println("Error sending message");
                    return null;
                });
    }

    /**
     *
     * @param messageHandler a consumer used with incoming NtfyMessageDto objects
     * @param topic current topic/chatroom will be added after client root adress
     * @return
     */
    @Override
    public CompletableFuture<Void> receive(Consumer<NtfyMessageDto> messageHandler, String topic) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/"+ topic + "/json"))
                .build();

        return http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                        .thenAccept(response -> response.body().forEach(line -> {
                            NtfyMessageDto message = mapper.readValue(line, NtfyMessageDto.class);
                            if ("message".equals(message.event())) {
                                    messageHandler.accept(message);
                                }
                        }))
                        .exceptionally(throwable -> {
                        System.out.println("Error receiving messages: " + throwable.getMessage());
                        return null;
                    });
    }
}
