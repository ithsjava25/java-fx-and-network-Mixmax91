package com.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private final StringProperty topic = new SimpleStringProperty("mytopic");

    public HelloModel(NtfyConnection connection) {

        this.connection = connection;

        observableMessages = FXCollections.observableArrayList();

        topic.addListener((obs, oldTopic, newTopic) -> {
            observableMessages.clear();
            startReceivingWithTopic(newTopic);
        });

        startReceivingWithTopic(topic.get());

    }

    private void startReceivingWithTopic(String topic) {
        connection.receive(message -> Platform.runLater(() -> addObservableMessage(message)), topic);
    }

    public String getTopic() {
        return topic.get();
    }

    public StringProperty topicProperty() {
        return topic;
    }

    public void setTopic(String newTopic) {
        topic.set(newTopic);
    }

    public ObservableList<NtfyMessageDto> getObservableMessages() {
        return observableMessages;
    }

    public void addObservableMessage(NtfyMessageDto dto) {
        observableMessages.add(dto);
    }

    public void sendToClient(String message) {
        connection.send(message, topicProperty().get());
    }

    public void sendAttachmentToClient(Path filePath, String fileType) {
        connection.sendAttachment(filePath, fileType, topicProperty().get());
    }
}

