package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Record to be used as Dto for incoming strings from client
 * @param id message id
 * @param time time when message was sent in Epoch
 * @param event type of incoming message
 * @param topic current chatroom
 * @param message message
 * @param attachment attachment
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NtfyMessageDto(String id, Long time, String event
        , String topic, String message, Attachment attachment) {}

