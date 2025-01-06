package com.example.utils

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.jackson.JacksonConverter

val objectMapper =
    jsonMapper {
        propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        addModule(kotlinModule())
    }

val appHttpClient by lazy {
    HttpClient(Java) {
        install(ContentNegotiation) {
            register(Json, JacksonConverter(objectMapper))
        }
    }
}