package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
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
    public boolean sendAttachment(Path filePath, String fileType) {
        try {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofFile(filePath))
                .uri(URI.create(hostName + "/JUV25D2"))
                .header("Filename", filePath.getFileName().toString())
                .build();


            //Todo: handle long blocking send request to not freeze JavaFx thread
            //TODO: Handle exception exceptionally?
            //TODO: download docker desktop
            //1. Use thread send message
            //2. Use async?
            var response = http.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException e) {
            System.out.println("Error sending message");
        } catch (InterruptedException e) {
            System.out.println("Interrupted sending message");
        }

        return true;
    }


    @Override
    public boolean send(String message) {

                HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/JUV25D2"))
                .build();

        try {
            //Todo: handle long blocking send request to not freeze JavaFx thread
            //TODO: Handle exception exceptionally?
            //TODO: download docker desktop
            //1. Use thread send message
            //2. Use async?
            var response = http.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException e) {
            System.out.println("Error sending message");
        } catch (InterruptedException e) {
            System.out.println("Interrupted sending message");
        }
        return true;
    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/JUV25D2/json"))
                .build();

        http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept( response -> response.body()
                        .map(line ->
                                mapper.readValue(line, NtfyMessageDto.class))
                        .filter(message -> message.event().equals("message"))
                        .forEach(messageHandler));

    }

//    public void extractMessage(NtfyMessageDto ntfyMessage) {
//        //TODO: Extract messages från datan
//
//        Platform.runLater(() -> observableMessages.add(ntfyMessage.message()));
//    }
}
