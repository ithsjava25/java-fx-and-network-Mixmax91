package com.example;

import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnector());

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @FXML
    public Label topicLabel;

    @FXML
    public ImageView hugeAnka;

    @FXML
    public VBox chatRoomsPanel;

    @FXML
    public Button menuButton;

    @FXML
    public TextField topicTextField;

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

    private final Random random = new Random();

    @FXML
    private void initialize() {
        topicLabel.textProperty().bind(model.topicProperty());
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


            private final Region spacer = new Region();

            private final VBox attachmentBox = new VBox();

            @Override
            protected void updateItem(NtfyMessageDto item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().add("cell-style"); //If I add transparancy inside the cell-style here, the cells doesn't block the background
                } else {
                    attachmentBox.getChildren().clear();

                    HBox messageBox = new HBox(10);
                    messageBox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label messageText = new Label(item.message());
                    messageText.setWrapText(true);
                    messageText.setMaxWidth(400);
                    messageText.getStyleClass().add("cell-style");

                    Label timeStamp = new Label(getFormattedString(item));
                    timeStamp.getStyleClass().add("time-stamp");

                    Button deleteButton = new Button("Delete");
                    deleteButton.getStyleClass().add("delete-button");

                    ImageView duck = new ImageView(new Image("duckcolored.png"));
                    duck.setFitWidth(26);
                    duck.setFitHeight(26);
                    duck.setPreserveRatio(true);
                    duck.setSmooth(true);
                    duck.setCache(true);

                    deleteButton.setOnAction(e -> {
                        model.getObservableMessages().remove(item);
                        dropTheDuck();
                    });

                    messageBox.getChildren().addAll(deleteButton, messageText, timeStamp, duck);

                    attachmentBox.getChildren().add(messageBox);
                    attachmentBox.setAlignment(Pos.CENTER_LEFT);
                    attachmentBox.setSpacing(10);

                    if(item.attachment() != null) {
                        if (item.attachment().type().startsWith("image")) {
                            ImageView attachmentImage = new ImageView(new Image(item.attachment().url()));
                            attachmentImage.setFitHeight(150);
                            attachmentImage.setPreserveRatio(true);
                            attachmentBox.getChildren().add(attachmentImage);
                        } else {
                            Button downloadButton = new Button("Download" +  item.attachment().name());
                            downloadButton.getStyleClass().add("download-button");
                            downloadButton.setOnAction(e -> {
                            try {
                                Desktop.getDesktop().browse(URI.create(item.attachment().url()));
                            } catch (IOException ex) {
                                System.out.println("Could not find file source");
                            }
                            });
                            attachmentBox.getChildren().add(downloadButton);
                        }
                    }
                    setGraphic(attachmentBox);
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            return;
        }

        if(checkIfFileToBig(file)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("File too big");
            alert.setHeaderText(null);
            alert.setContentText("Cannot send files bigger than 15 MB");
            alert.showAndWait();
            return;
        }
        try {
            if (file.exists()) {
                Path filePath = file.toPath();

                String fileType = Files.probeContentType(filePath);

                model.sendAttachmentToClient(filePath, fileType);

            } else {
                System.out.println("Couldn't find file");
            }
        } catch (IOException e) {
            System.out.println("Couldn't find file");
        }

    }

    private boolean checkIfFileToBig(File file) {
        if (file.isFile()){
            long maxSize = 15L * 1024 * 1024;
            if (file.length() > maxSize) {
                return true;
            }
        }
        return false;
    }

    public Button getAttachmentButton() {
        return attachmentButton;
    }

    public void setAttachmentButton(Button attachmentButton) {
        this.attachmentButton = attachmentButton;
    }

    public void closeWindow(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void switchToMytopic(ActionEvent actionEvent) {
        model.topicProperty().set("mytopic");
    }

    public void switchToJUV25D(ActionEvent actionEvent) {
        model.topicProperty().set("JUV25D");
    }

    public void switchToITHS(ActionEvent actionEvent) {
        model.topicProperty().set("ITHS");
    }

    public void switchToRandom(ActionEvent actionEvent) {
        model.topicProperty().set("Chatroom" + random.nextInt(10000));
    }

    public void openMenu(ActionEvent actionEvent) {
        chatRoomsPanel.setVisible(!chatRoomsPanel.isVisible());
    }

    public void customTopic(ActionEvent actionEvent) {
        String newTopic = topicTextField.getText();
        model.topicProperty().set(newTopic);
    }
}
