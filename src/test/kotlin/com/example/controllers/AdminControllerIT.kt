package com.example.controllers

import com.example.configureTestEnvironment
import com.example.dtos.AccessStatisticDto
import com.example.repository.FactRepository
import com.example.services.FactService
import com.example.setupTestEnvironment
import com.example.utils.AppConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerIT {

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
    fun `GET admin statistics with valid API key should return statistics`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        runBlocking {
            testRepository.storeFact("abcd1234", "Fact 1", "https://example.com/fact1")
            testRepository.storeFact("efgh5678", "Fact 2", "https://example.com/fact2")
        }

        val response = client.get("/admin/statistics") {
            contentType(ContentType.Application.Json)
            header("X-API-KEY", testConfig.apiKey)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()

        // deserialize the response into a List of AccessStatisticDto
        val statistics: List<AccessStatisticDto> = jacksonObjectMapper().readValue(
            responseBody,
            jacksonObjectMapper().typeFactory.constructCollectionType(List::class.java, AccessStatisticDto::class.java)
        )
        assert(statistics.isNotEmpty())
    }

    @Test
    fun `GET admin statistics with invalid API key should return unauthorized`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        val response = client.get("/admin/statistics") {
            contentType(ContentType.Application.Json)
            headers {
                append("X-API-KEY", "invalid-api-key") // Pass an invalid API key
            }
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseBody = response.bodyAsText()
        assertEquals("Unauthorized", responseBody)
    }

    @Test
    fun `GET admin statistics without API key should return unauthorized`() = testApplication {
        application {
            configureTestEnvironment(testConfig, testService)
        }

        val response = client.get("/admin/statistics") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        val responseBody = response.bodyAsText()
        assertEquals("Unauthorized", responseBody)
    }
}
