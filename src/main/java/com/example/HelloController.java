package com.example;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    public ImageView hugeAnka;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView ankImage;

    @FXML
    private Button sendButton;


    @FXML
    private TextField textField;

    @FXML
    private ListView<String> listView;


    @FXML
    private void initialize() {

        hugeAnka.setImage(new Image("duck.png"));
        hugeAnka.setVisible(false);
        ankImage.setImage(new Image("duckcolored.png"));
        listView.setItems(model.getObservableMessages());
        sendButton.disableProperty().bind(textField.textProperty().isEmpty());

        listView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                Button deleteButton = new Button("Delete");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(e -> {;
                        model.getObservableMessages().remove(item);
                        dropTheDuck();
                });


                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().add("cell-style"); //If I add transparancy here, the cells doesn't block the background
                } else {
                    setText(item);
                    setGraphic(deleteButton);

                    if (!getStyleClass().contains("cell-style")) {
                        getStyleClass().add("cell-style");
                    }
                }
            }
        });
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
        launchTheDuck();
        textField.clear();
    }

    private void launchTheDuck() {
        ankImage.setTranslateY(0);
        TranslateTransition launch = new TranslateTransition();
        launch.setNode(ankImage);
        launch.setDuration(Duration.seconds(0.5));
        launch.setByY(-500);
        launch.play();

    }
    private void dropTheDuck() {
        ankImage.setTranslateY(-500);
        TranslateTransition drop = new TranslateTransition();
        drop.setNode(ankImage);
        drop.setDuration(Duration.seconds(0.5));
        drop.setByY(500);
        drop.play();
    }

    public void makeAnkaVisible(MouseEvent mouseEvent) {
        hugeAnka.setVisible(!hugeAnka.isVisible());
    }
}
