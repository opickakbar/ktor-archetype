package com.example

import com.example.controllers.configureAdminController
import com.example.controllers.configureFactController
import com.example.services.FactService
import com.example.utils.AppConfig
import com.example.utils.objectMapper
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        jackson { objectMapper }
    }
    val appConfig = AppConfig(this)
    val factService = FactService(appConfig)
    configureFactController(factService)
    configureAdminController(factService, appConfig)
}
