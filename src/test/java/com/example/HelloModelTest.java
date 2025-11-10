package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.SystemProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class HelloModelTest {

    @Test
    @DisplayName("Given a model with a path and a file type to send when calling sendAttachmentToClient then assert that it called connection.sendAttachment")
    void sendAttachmentToClientCallsConnectionWithPathAndType() {
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);

        Path mockFilePath = Path.of("C:/Users/");
        String fileType = "image/png";

        model.sendAttachmentToClient(mockFilePath, fileType);

        assertThat(spy.filePath).isEqualTo(Path.of("C:/Users/"));
        assertThat(spy.fileType).isEqualTo("image/png");

    }

    @Test
    @DisplayName("Given a model with a message to send when calling sendToClient then assert that it called connection.send")
    void sendToClientCallsConnectionWithMessageToSend() {
        //Arrange
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);
        //Act
        model.sendToClient("Hello World");
        //Assert
        assertThat(spy.message).isEqualTo("Hello World");
    }

    @Test
    @DisplayName("When given a model with connection to mock server when sending http post request with body then verify that it calls server with correct url and body")
    void sendMessageToFakeServer(WireMockRuntimeInfo wmRuntimeInfo) {
        var connection = new NtfyConnector("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new HelloModel(connection);
        model.setTopic("JUV25D");

        stubFor(post("/JUV25D").willReturn(ok()));
        model.sendToClient("Hello World").join();

        //Verify call made to server
        verify(postRequestedFor(urlEqualTo("/JUV25D"))
                .withRequestBody(equalTo("Hello World")));
    }

    @Test
    @DisplayName("When given a model with connection to mock server when sending http post request with body and a 5second delay then verify the time it took")
    void sendMessageToFakeServerWithDelay(WireMockRuntimeInfo wmRuntimeInfo) {
        var connection = new NtfyConnector("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new HelloModel(connection);
        model.setTopic("JUV25D");

        stubFor(post("/JUV25D").willReturn(aResponse()
                .withFixedDelay(5000)));

        long delayStart = System.currentTimeMillis();
        model.sendToClient("Hello World").join();
        long delayDuration = System.currentTimeMillis() - delayStart;

        assertThat(delayDuration).isGreaterThanOrEqualTo(5000);

        //Verify call made to server
        verify(postRequestedFor(urlEqualTo("/JUV25D"))
                .withRequestBody(equalTo("Hello World")));
    }

    @Test
    void addObservableMessageAddsDtoAndGetObservableMessagesReturnsCorrectList() {
        var model = new HelloModel(new NtfyConnectionSpy());
        ObservableList<NtfyMessageDto> mockObservableMessages = FXCollections.observableArrayList();

        NtfyMessageDto mockDto = new NtfyMessageDto(
                "id", 1111L, "event", "topic", "message"
                , (new Attachment("name", "type", 2222L, 3333L, "url")));

        model.addObservableMessage(mockDto);
        mockObservableMessages = model.getObservableMessages();
        assertThat(mockObservableMessages).hasSize(1);
        assertThat(mockObservableMessages).contains(mockDto);
    }

    @Test
    void addObservableMessageCanAddSeveralDto() {
        var model = new HelloModel(new NtfyConnectionSpy());
        ObservableList<NtfyMessageDto> mockObservableMessages = FXCollections.observableArrayList();

        NtfyMessageDto mockDto = new NtfyMessageDto(
                "id", 1111L, "event", "topic", "message"
                , (new Attachment("name", "type", 2222L, 3333L, "url")));
        NtfyMessageDto mockDto2 = new NtfyMessageDto(
                "id2", 11112L, "event2", "topic2", "message2"
                , (new Attachment("name2", "type2", 22222L, 33332L, "url2")));

        model.addObservableMessage(mockDto);
        model.addObservableMessage(mockDto2);
        mockObservableMessages = model.getObservableMessages();
        assertThat(mockObservableMessages).hasSize(2);
        assertThat(mockObservableMessages).contains(mockDto);
        assertThat(mockObservableMessages).contains(mockDto2);
    }

    @Test
    void getObservableMessageCanRemoveItem() {
        var model = new HelloModel(new NtfyConnectionSpy());
        ObservableList<NtfyMessageDto> mockObservableMessages = FXCollections.observableArrayList();

        NtfyMessageDto mockDto = new NtfyMessageDto(
                "id", 1111L, "event", "topic", "message"
                , (new Attachment("name", "type", 2222L, 3333L, "url")));
        NtfyMessageDto mockDto2 = new NtfyMessageDto(
                "id2", 11112L, "event2", "topic2", "message2"
                , (new Attachment("name2", "type2", 22222L, 33332L, "url2")));

        model.addObservableMessage(mockDto);
        model.addObservableMessage(mockDto2);
        model.getObservableMessages().remove(mockDto2);
        mockObservableMessages = model.getObservableMessages();
        assertThat(mockObservableMessages).hasSize(1);
        assertThat(mockObservableMessages).contains(mockDto);
        assertThat(mockObservableMessages).doesNotContain(mockDto2);
    }



//    @Test
//    void sendGetRequestToFakeServer(WireMockRuntimeInfo wmRuntimeInfo){
//        var connection = new NtfyConnector("http://localhost:" + wmRuntimeInfo.getHttpPort());
//        var model = new HelloModel(connection);
//        stubFor(get("/JUV25D2/json").willReturn(ok()));
//        model.receiveMessages();
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        verify(getRequestedFor(urlEqualTo("/JUV25D2/json")));
//    }

}