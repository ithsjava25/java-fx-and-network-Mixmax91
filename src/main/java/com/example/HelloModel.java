package com.example;


import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class HelloModel {

    private ObservableList<String> observableMessages;

    private final String hostName;

    public HelloModel() {

        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));

        observableMessages = FXCollections.observableArrayList();
    }

    public ObservableList<String> getObservableMessages() {
        return observableMessages;
    }

    public void setObservableMessages(ObservableList<String> observableMessages) {
        this.observableMessages = observableMessages;
    }

    public void addMessage(String message) {
        observableMessages.add(message);
        sendToClient(message);

        //Todo: send message with https

    }

    public void sendToClient(String message) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/mytopic"))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.out.println("Error sending message");
        } catch (InterruptedException e) {
            System.out.println("Interrupted sending message");
        }

    }




}
