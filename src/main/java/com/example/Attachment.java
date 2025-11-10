package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Used for parsing attachment json into Dto
 * @param name
 * @param type
 * @param size
 * @param expires
 * @param url
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AttachmentDto(String name, String type, long size, long expires, String url){}
