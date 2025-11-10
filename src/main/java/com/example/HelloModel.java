package com.example;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class HelloModel {

    private final NtfyConnection connection;
    private final ObservableList<NtfyMessageDto> observableMessages;
    private final StringProperty topic;

    public HelloModel(NtfyConnection connection) {
        this.connection = connection;

        observableMessages = FXCollections.observableArrayList();

        topic = new SimpleStringProperty("AnkChat");

        //Clears messages from chat and sends a new get request to client with the correct topic/chatroom
        topic.addListener((obs, oldTopic, newTopic) -> {
            observableMessages.clear();
            startReceivingWithTopic(newTopic);
        });

        //Default starting get request
        startReceivingWithTopic(topic.get());
    }

    /**
     *
     * @param topic the new chatroom to be sent to client
     * @return CompletableFuture that is used for asynchronous calls and tests
     */
    public CompletableFuture<Void> startReceivingWithTopic(String topic) {
        return connection.receive(message -> Platform.runLater(() -> addObservableMessage(message)), topic);
    }

    /**
     *
     * @return the current topic/chatroom as a String
     */
    public String getTopic() {
        return topic.get();
    }

    /**
     *
     * @return the current chatroom/topics StringProperty
     */
    public StringProperty topicProperty() {
        return topic;
    }

    /**
     *
     * @param newTopic sets a new chatroom/topic to StringProperty
     */
    public void setTopic(String newTopic) {
        topic.set(newTopic);
    }

    /**
     *
     * @return Dto containing: String id, Long time, String event
     *         , String topic, String message, Attachment attachment
     */
    public ObservableList<NtfyMessageDto> getObservableMessages() {
        return observableMessages;
    }

    /**
     *
     * @param dto adds to list of messages that is binded with ListView
     */
    public void addObservableMessage(NtfyMessageDto dto) {
        observableMessages.add(dto);
    }

    /**
     *
     * @param message String message to send to client
     * @return CompletableFuture for asynchronous calls and tests
     */
    public CompletableFuture<Void> sendToClient(String message) {
        return connection.send(message, topicProperty().get());
    }

    /**
     *
     * @param filePath path to file being sent to client
     * @param fileType file type of the file as a string
     * @return CompletableFuture for asynchronous calls and tests
     */
    public CompletableFuture<Void> sendAttachmentToClient(Path filePath, String fileType) {
        return connection.sendAttachment(filePath, fileType, topicProperty().get());
    }
}

