package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private Label messageLabel;

    @FXML
    private Button sendButton;

    @FXML
    private TextField textField;

    @FXML
    private ListView<String> listView;


    @FXML
    private void initialize() {
        listView.setItems(model.getObservableMessages());
        sendButton.disableProperty().bind(textField.textProperty().isEmpty());
    }

    public HelloModel getModel(){
        return model;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public void setSendButton(Button sendButton) {
        this.sendButton = sendButton;
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = textField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        model.addMessage(message);
        System.out.println(model.getObservableMessages().toString());
        listView.scrollTo(model.getObservableMessages().size() - 1);
        textField.clear();
    }
}
