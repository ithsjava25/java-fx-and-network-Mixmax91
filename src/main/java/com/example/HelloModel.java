package com.example;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class HelloModel {

    private ObservableList<String> observableMessages;


    public HelloModel() {
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
    }




}
