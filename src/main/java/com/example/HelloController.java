package com.example;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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

    /**
     *
     * @param actionEvent when enter or send button is clicked, when textField is not empty
     *                    then sends text inside textField to client
     */
    public void sendMessage(ActionEvent actionEvent) {
        String message = textField.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        //Clear and disable textField, set prompt message, when waiting for server
        textField.clear();
        textField.setDisable(true);
        textField.setPromptText("Sending message");

        model.sendToClient(message)
                .thenRun(()-> {
                    Platform.runLater(() -> {
                        listView.scrollTo(model.getObservableMessages().size() - 1);
                        launchTheDuck();
                        textField.setPromptText("Quack..."); //Then
                        textField.setDisable(false);
                    });
                }).exceptionally(throwable -> {
                    System.out.println("Error sending message");
                    return null;
                });
    }

    /**
     * Launches small duck from left bottom corner
     */
    private void launchTheDuck() {
        ankImage.setTranslateY(0);
        TranslateTransition launch = new TranslateTransition();
        launch.setNode(ankImage);
        launch.setDuration(Duration.seconds(0.5));
        launch.setByY(-500);
        launch.play();
    }

    /**
     * Drops small duck from top left of window
     */
    private void dropTheDuck() {
        ankImage.setTranslateY(-500);
        TranslateTransition drop = new TranslateTransition();
        drop.setNode(ankImage);
        drop.setDuration(Duration.seconds(0.5));
        drop.setByY(500);
        drop.play();
    }

    /**
     *
     * @param mouseEvent when clicking on small duck, then make the big duck visible
     */
    public void makeAnkaVisible(MouseEvent mouseEvent) {
        hugeAnka.setVisible(!hugeAnka.isVisible());
    }

    /**
     * Creates a cell based on css file
     * Contains an VBox and an HBox with a label, timestamp, delete button and a small duck
     * VBox is used to display attachment under the text, if it is an image, presents a preview in an
     * imageView, if not an image, a Download button is created next to filename
     */
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
                    messageText.setMaxWidth(314);
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

                            ImageView attachmentImage;
                            try {
                                    attachmentImage = new ImageView(new Image(item.attachment().url(), true));
                                } catch (Exception e) {
                                    attachmentImage = new ImageView(new Image("placeholder.png"));
                                }
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

    /**
     *
     * @param item extracts the time and converts it from epoch to LocalDateTime
     * @return Time in yyyy-MM-dd HH:mm:ss as a string
     */
    private String getFormattedString(NtfyMessageDto item) {
        Instant instant = Instant.ofEpochSecond(item.time());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }

    /**
     *
     * @param actionEvent when attachmentButton is clicked, opens the file chooser
     *                    if chosen file is bigger than 15 MB an Alert WARNING is triggered
     *                    with showAndWait
     *                    then sends file to client
     */
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
                if (fileType == null) {
                        fileType = "application/octet-stream"; // default MIME type
                    }

                String finalFileType = fileType;
                model.sendAttachmentToClient(filePath, fileType)
                            .thenRun(() -> Platform.runLater(() -> {
                            // Success feedback
                                    textField.setPromptText("Attachment sent!");
                        }))
                            .exceptionally(error -> {
                            Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Upload Failed");
                                    alert.setContentText("Could not send attachment: " + error.getMessage());
                                    alert.show();
                                });
                            return null;
                        });

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText("Unable to open the file");
                alert.setContentText("The file could not be found or opened.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Error");
            alert.setHeaderText("Unable to open the file");
            alert.setContentText("The file could not be found or opened.");
            alert.showAndWait();
        }

    }

    /**
     *
     * @param file chosen file from file chooser
     * @return true if file is bigger than 15 MB, otherwise false
     */
    private boolean checkIfFileToBig(File file) {
        if (file.isFile()){
            long maxSize = 15L * 1024 * 1024;
            if (file.length() > maxSize) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param actionEvent when button is clicked inside chatroom menu
     *                    then change stringProperty to "mytopic"
     */
    public void switchToMytopic(ActionEvent actionEvent) {
        model.topicProperty().set("mytopic");
    }

    /**
     *
     * @param actionEvent when button is clicked inside chatroom menu
     *                    then change stringProperty to "JUV25D"
     */
    public void switchToJUV25D(ActionEvent actionEvent) {
        model.topicProperty().set("JUV25D");
    }

    /**
     *
     * @param actionEvent when button is clicked inside chatroom menu
     *                    then change stringProperty to "ITHS"
     */
    public void switchToITHS(ActionEvent actionEvent) {
        model.topicProperty().set("ITHS");
    }

    /**
     *
     * @param actionEvent when button is clicked inside chatroom menu
     *                    then change stringProperty to
     *                    "Chatroom" + a random number between 0 inclusive and 10000 exclusive
     */
    public void switchToRandom(ActionEvent actionEvent) {
        model.topicProperty().set("Chatroom" + random.nextInt(10000));
    }

    /**
     *
     * @param actionEvent when clicking the menuButton
     *                    then make chatRoomsPanel visible
     *                    if visible already, make invisible
     */
    public void openMenu(ActionEvent actionEvent) {
        chatRoomsPanel.setVisible(!chatRoomsPanel.isVisible());
    }

    /**
     *
     * @param actionEvent when pressing enter in the topicTextField
     *                    change stringProperty topic to text inside the field
     */
    public void customTopic(ActionEvent actionEvent) {
        String newTopic = topicTextField.getText();
        model.topicProperty().set(newTopic);
    }
}
