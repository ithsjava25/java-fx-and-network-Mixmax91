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

public class NtfyConnector implements NtfyConnection{
    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();

    public NtfyConnector(){
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    public NtfyConnector(String hostName){
        this.hostName = hostName;
    }

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
        } catch (IOException e) {
            System.out.println("Error sending message");
            return null;
        }
    }


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

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler, String topic) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/"+ topic + "/json"))
                .build();

        http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept( response -> response.body()
                        .map(line ->
                                mapper.readValue(line, NtfyMessageDto.class))
                        .filter(message -> message.event().equals("message"))
                        .forEach(messageHandler));

    }
}
