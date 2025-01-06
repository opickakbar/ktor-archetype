package com.example.controllers

import com.example.services.FactService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureFactController(factService: FactService) {
    routing {
        post("/facts") {
            try {
                val shortenedUrlDto = factService.fetchAndShortenFacts()
                call.respond(HttpStatusCode.OK, shortenedUrlDto)
            } catch (e: Exception) {
                log.error("Error while processing /facts request", e)
                call.respondText("Failed to fetch and shorten a fact: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
        get("/facts/{shortenedUrl}") {
            val shortenedUrl: String? = call.parameters["shortenedUrl"]
            if (shortenedUrl.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Shortened URL is missing")
                return@get
            }
            val factDto = factService.getFact(shortenedUrl)
            if (factDto != null) {
                call.respond(HttpStatusCode.OK, factDto)
            } else {
                call.respond(HttpStatusCode.NotFound, "Fact not found for shortened URL: $shortenedUrl")
            }
        }
        get("/facts") {
            try {
                val allFacts = factService.getAllFacts()
                call.respond(HttpStatusCode.OK, allFacts)
            } catch (e: Exception) {
                call.respondText("Failed to retrieve cached facts: ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
        get("/facts/{shortenedUrl}/redirect") {
            val shortenedUrl = call.parameters["shortenedUrl"]
            if (shortenedUrl.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Shortened URL is missing")
                return@get
            }

            val permalink = factService.getOriginalPermalink(shortenedUrl)
            if (permalink != null) {
                call.respondRedirect(permalink, permanent = false)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    "Permalink not found for shortened URL: $shortenedUrl"
                )
            }
        }
    }
}
