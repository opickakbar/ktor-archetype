package com.example.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ShortenedUrlDto(
    @JsonProperty("original_fact")
    val originalFact: String,
    @JsonProperty("shortened_url")
    val shortenedUrl: String,
)

data class FactDto(
    @JsonProperty("fact")
    val fact: String,
    @JsonProperty("original_permalink")
    val originalPermalink: String,
)

data class AccessStatisticDto(
    @JsonProperty("shortened_url")
    val shortenedUrl: String,
    @JsonProperty("access_count")
    val accessCount: Int
)

data class UselessFactResponseDto(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("text")
    val text: String,
    @JsonProperty("source")
    val source: String,
    @JsonProperty("source_url")
    val sourceUrl: String,
    @JsonProperty("language")
    val language: String,
    @JsonProperty("permalink")
    val permalink: String
)