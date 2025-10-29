package com.example;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class HelloModel {

//    private List<String> messageLog;
    private ObservableList<String> observableMessages;

    public HelloModel() {
        //messageLog = new ArrayList<>();
        observableMessages = FXCollections.observableArrayList();
    }

//    public List<String> getMessageLog() {
//        return messageLog;
//    }
//
//    public void setMessageLog(List<String> messageLog) {
//        this.messageLog = messageLog;
//    }

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
