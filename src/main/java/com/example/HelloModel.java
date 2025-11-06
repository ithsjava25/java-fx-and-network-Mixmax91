package com.example;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;

/**
 * TODO: Spara meddelanden i fil
 * TODO: Threads?
 * TODO: Exception tappa anslutning?
 * TODO: Gör så att man kan välja topicnamn
 * TODO: Make app side scrollable
 */

public class HelloModel {


    private final NtfyConnection connection;

    private final ObservableList<NtfyMessageDto> observableMessages;

    public HelloModel(NtfyConnection connection) {

        this.connection = connection;

        observableMessages = FXCollections.observableArrayList();
        receiveMessages();
    }

    public ObservableList<NtfyMessageDto> getObservableMessages() {
        return observableMessages;
    }

    public void addObservableMessage(NtfyMessageDto dto) {
        observableMessages.add(dto);
    }


    public void sendToClient(String message) {
        connection.send(message);
    }

    public void receiveMessages() {
        connection.receive(s -> Platform.runLater(() -> addObservableMessage(s)));
    }

    public void sendAttachmentToClient(Path filePath, String fileType) {
        connection.sendAttachment(filePath, fileType);
    }
}

