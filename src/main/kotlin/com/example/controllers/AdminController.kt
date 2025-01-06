package com.example.controllers

import com.example.services.FactService
import com.example.utils.AppConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAdminController(factService: FactService, appConfig: AppConfig) {
    routing {
        get("/admin/statistics") {
            val apiKey = call.request.headers["X-API-KEY"]
            if (apiKey != appConfig.apiKey) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@get
            }
            try {
                val statistics = factService.getAccessStatistics()
                call.respond(HttpStatusCode.OK, statistics)
            } catch (e: Exception) {
                call.respondText("Failed to retrieve access statistics: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
