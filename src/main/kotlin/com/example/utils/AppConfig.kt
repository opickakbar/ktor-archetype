package com.example.utils

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig

class AppConfig(applicationConfig: ApplicationConfig) {
    constructor(application: Application) : this(application.environment.config)
    val baseUrl: String = applicationConfig.property("ktor.application.baseUrl").getString()
    val uselessFactApiUrl: String = applicationConfig.property("ktor.application.uselessFactApiUrl").getString()
    val apiKey: String = applicationConfig.property("ktor.application.apiKey").getString()
}