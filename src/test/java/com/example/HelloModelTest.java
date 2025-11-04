package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class HelloModelTest {

    @Test
    @DisplayName("Given a model with messageToSend when calling sendToClient then assert that it called connection.send")
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
    void sendMessageToFakeServer(WireMockRuntimeInfo wmRuntimeInfo) {
        var connection = new NtfyConnector("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new HelloModel(connection);
        stubFor(post("/JUV25D2").willReturn(ok()));
        model.sendToClient("Hello World");

        //Verify call made to server
        verify(postRequestedFor(urlEqualTo("/JUV25D2"))
                .withRequestBody(equalTo("Hello World")));
    }

}