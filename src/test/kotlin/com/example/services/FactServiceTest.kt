package com.example.services


import com.example.dtos.AccessStatisticDto
import com.example.dtos.FactDto
import com.example.repository.FactRepository
import com.example.utils.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.jackson.jackson
import io.ktor.server.config.MapApplicationConfig
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FactServiceTest {

    private lateinit var factService: FactService
    private lateinit var factRepository: FactRepository
    private lateinit var appConfig: AppConfig
    private lateinit var httpClient: HttpClient

    @BeforeEach
    fun setup() {
        factRepository = mockk(relaxed = true)
        val mockConfig = MapApplicationConfig(
            "ktor.application.baseUrl" to "http://localhost:8080",
            "ktor.application.uselessFactApiUrl" to "https://uselessfacts.jsph.pl/random.json?language=en",
            "ktor.application.apiKey" to "test"
        )
        appConfig = AppConfig(mockConfig)

        // mock HTTP client with MockEngine
        val mockEngine = MockEngine { request ->
            respond(
                content = """{
                    "id": "12345",
                    "text": "This is a test fact",
                    "source": "source",
                    "source_url": "https://example.com/source",
                    "language": "en",
                    "permalink": "https://example.com/fact/12345"
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                jackson()
            }
        }
        factService = FactService(appConfig, httpClient, factRepository)
    }

    @Test
    fun `fetchAndShortenFacts should fetch a fact and store it in the repository`() = runBlocking {
        // when calling the service
        val result = factService.fetchAndShortenFacts()

        // then: verify repository interactions and assertions
        coVerify { factRepository.storeFact(any(), "This is a test fact", "https://example.com/fact/12345") }
        assertEquals("This is a test fact", result.originalFact)
        assert(result.shortenedUrl.startsWith("http://localhost:8080/facts/"))
    }

    @Test
    fun `getFact should retrieve a fact and increment access count`() = runBlocking {
        // given
        val shortenedUrl = "abcd1234"
        val factDto = FactDto("This is a test fact", "https://example.com/fact/abcd1234")
        every { factRepository.retrieveFact(shortenedUrl) } returns factDto

        // when
        val result = factService.getFact(shortenedUrl)

        // then
        verify { factRepository.incrementAccessCount(shortenedUrl) }
        assertEquals(factDto, result)
    }

    @Test
    fun `getFact should return null if fact does not exist`() = runBlocking {
        // given
        val shortenedUrl = "non-existent-url"
        every { factRepository.retrieveFact(shortenedUrl) } returns null

        // when
        val result = factService.getFact(shortenedUrl)

        // then
        verify(exactly = 0) { factRepository.incrementAccessCount(any()) }
        assertNull(result)
    }

    @Test
    fun `getAllFacts should return all facts from the repository`() = runBlocking {
        // given
        val facts = listOf(
            FactDto("Fact 1", "https://example.com/fact1"),
            FactDto("Fact 2", "https://example.com/fact2")
        )
        every { factRepository.retrieveAllFacts() } returns facts

        // when
        val result = factService.getAllFacts()

        // then
        assertEquals(facts, result)
    }

    @Test
    fun `getOriginalPermalink should retrieve a permalink and increment access count`() = runBlocking {
        // given
        val shortenedUrl = "abcd1234"
        val permalink = "https://example.com/fact/abcd1234"
        every { factRepository.retrievePermalink(shortenedUrl) } returns permalink

        // when
        val result = factService.getOriginalPermalink(shortenedUrl)

        // then
        verify { factRepository.incrementAccessCount(shortenedUrl) }
        assertEquals(permalink, result)
    }

    @Test
    fun `getOriginalPermalink should return null if permalink does not exist`() = runBlocking {
        val shortenedUrl = "non-existent-url"
        every { factRepository.retrievePermalink(shortenedUrl) } returns null
        val result = factService.getOriginalPermalink(shortenedUrl)
        verify(exactly = 0) { factRepository.incrementAccessCount(any()) }
        assertNull(result)
    }

    @Test
    fun `getAccessStatistics should return statistics from the repository`() = runBlocking {
        // given
        val statistics = listOf(
            AccessStatisticDto("http://localhost:8080/facts/abcd1234", 10),
            AccessStatisticDto("http://localhost:8080/facts/efgh5678", 5)
        )
        every { factRepository.getAccessStatistics(appConfig) } returns statistics

        // when
        val result = factService.getAccessStatistics()

        // then
        assertEquals(statistics, result)
    }
}
