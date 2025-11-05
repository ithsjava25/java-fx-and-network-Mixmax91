package com.example;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.format.DateTimeFormatter;

/**
 * TODO: Spara meddelanden i fil
 * TODO: Lägg till tidstämpel på meddelanden
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

    public void sendToClient(String message) {
        connection.send(message);

    }

    public void receiveMessages() {
        connection.receive(s -> Platform.runLater(() -> observableMessages.add(s)));
    }
}

