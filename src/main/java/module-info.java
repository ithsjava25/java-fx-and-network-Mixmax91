module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.github.cdimascio.dotenv.java;
    requires java.net.http;

    opens com.example to javafx.fxml;
    exports com.example;
}