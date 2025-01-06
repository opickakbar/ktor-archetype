package com.example.controllers

import com.example.configureTestEnvironment
import com.example.dtos.FactDto
import com.example.repository.FactRepository
import com.example.services.FactService
import com.example.setupTestEnvironment
import com.example.utils.AppConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FactControllerIT {

    private lateinit var testConfig: AppConfig
    private lateinit var testService: FactService
    private lateinit var testRepository: FactRepository

    @BeforeAll
    fun setup() {
        val environment = setupTestEnvironment()
        testConfig = environment.first
        testService = environment.second
        testRepository = environment.third
    }

    @Test
    fun `POST facts should create a shortened URL and store fact`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        val response = client.post("/facts") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText() // Read the response as raw text first
        val deserializedBody = jacksonObjectMapper().readValue(responseBody, Map::class.java)

        assert(deserializedBody["original_fact"] != null)
        assert(deserializedBody["shortened_url"] != null)
        testRepository.clear()
    }

    @Test
    fun `GET facts should return all stored facts`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        runBlocking {
            testRepository.storeFact("abcd1234", "Fact 1", "https://example.com/fact1")
            testRepository.storeFact("efgh5678", "Fact 2", "https://example.com/fact2")
        }

        val response = client.get("/facts") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText() // Get raw JSON response
        val facts = jacksonObjectMapper().readValue(responseBody, List::class.java)

        assertEquals(2, facts.size)
        testRepository.clear()
    }

    @Test
    fun `GET facts by shortened URL should return specific fact`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        runBlocking {
            testRepository.storeFact("abcd1234", "Fact 1", "https://example.com/fact1")
        }

        val response = client.get("/facts/abcd1234") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val factDto = jacksonObjectMapper().readValue(responseBody, FactDto::class.java)

        assertEquals("Fact 1", factDto.fact)
        assertEquals("https://example.com/fact1", factDto.originalPermalink)
        testRepository.clear()
    }

    @Test
    fun `GET facts by invalid shortened URL should return 404`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        val response = client.get("/facts/non-existent") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        testRepository.clear()
    }
}
