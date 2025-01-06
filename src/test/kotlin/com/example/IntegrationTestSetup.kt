package com.example

import com.example.controllers.configureAdminController
import com.example.controllers.configureFactController
import com.example.repository.FactRepository
import com.example.services.FactService
import com.example.utils.AppConfig
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

fun setupTestEnvironment(): Triple<AppConfig, FactService, FactRepository> {
    val factRepository = FactRepository()
    val mockConfig = MapApplicationConfig(
        "ktor.application.baseUrl" to "http://localhost:8080",
        "ktor.application.uselessFactApiUrl" to "https://uselessfacts.jsph.pl/random.json?language=en",
        "ktor.application.apiKey" to "testing-only"
    )
    val appConfig = AppConfig(mockConfig)
    val factService = FactService(appConfig, factRepository = factRepository)
    return Triple(appConfig, factService, factRepository)
}

fun Application.configureTestEnvironment(appConfig: AppConfig, factService: FactService) {
    install(ContentNegotiation) {
        jackson()
    }
    routing {
        configureFactController(factService)
        configureAdminController(factService, appConfig)
    }
}
