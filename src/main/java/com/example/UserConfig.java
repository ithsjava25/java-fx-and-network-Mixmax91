package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public final class UserConfig {

    private final StringProperty username = new SimpleStringProperty(this, "username", "");
    private final StringProperty topic = new SimpleStringProperty(this, "topic", "");
    private final StringProperty password = new SimpleStringProperty(this, "password", "");

    public UserConfig(String topic) {
        this.topic.set(topic);
    }

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    public String getTopic() { return topic.get(); }
    public void setTopic(String topic) { this.topic.set(topic); }
    public StringProperty topicProperty() { return topic; }

    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserConfig that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(topic, that.topic) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, topic, password);
    }
}
