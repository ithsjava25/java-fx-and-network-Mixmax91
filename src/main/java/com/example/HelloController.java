package com.example;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnector());

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public ImageView hugeAnka;

    @FXML
    private ImageView ankImage;

    @FXML
    private Button sendButton;

    @FXML
    private Button attachmentButton;

    @FXML
    private TextField textField;

    @FXML
    private ListView<NtfyMessageDto> listView;


    @FXML
    private void initialize() {

        hugeAnka.setImage(new Image("duck.png"));
        hugeAnka.setVisible(false);
        ankImage.setImage(new Image("duckcolored.png"));
        listView.setItems(model.getObservableMessages());
        sendButton.disableProperty().bind(textField.textProperty().isEmpty());
        cellFactoryCreator();
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

        model.sendToClient(message);
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

    private void cellFactoryCreator() {
        listView.setCellFactory(listView -> new ListCell<>() {

            private final HBox messageBox = new HBox(10);
            private final Region spacer = new Region();
            private final Label messageText = new Label();
            private final Label timeStamp = new Label();
            private final Button deleteButton = new Button("Delete");

            {
                messageText.getStyleClass().add("cell-style");
                timeStamp.getStyleClass().add("time-stamp");
                deleteButton.getStyleClass().add("delete-button");

                HBox.setHgrow(spacer, Priority.ALWAYS);
                messageBox.getChildren().addAll(deleteButton, messageText, timeStamp);
                messageBox.setAlignment(Pos.CENTER_LEFT);
            }


            @Override
            protected void updateItem(NtfyMessageDto item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().add("cell-style"); //If I add transparancy inside the cell-style here, the cells doesn't block the background
                } else {
                    messageText.setText(item.message());
                    timeStamp.setText(getFormattedString(item));
                    deleteButton.setOnAction(e -> {
                        model.getObservableMessages().remove(item);
                        dropTheDuck();
                    });

                    setGraphic(messageBox);
                }


            }
        });
    }

    private String getFormattedString(NtfyMessageDto item) {
        Instant instant = Instant.ofEpochSecond(item.time());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }

    public void attachFile(ActionEvent actionEvent) {
        System.out.println("Pressing attachment");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(null);
        try {
            if (file != null) {
                Path filePath = file.toPath();

                String fileType = Files.probeContentType(filePath);

                model.sendAttachmentToClient(filePath, fileType);

            } else {
                System.out.println("Couldnt find file");
            }
        } catch (IOException e) {
            System.out.println("Couldnt find file");
        }

    }
}
