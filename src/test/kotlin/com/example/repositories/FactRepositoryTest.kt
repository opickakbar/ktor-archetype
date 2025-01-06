package com.example.repositories

import com.example.dtos.AccessStatisticDto
import com.example.dtos.FactDto
import com.example.repository.FactRepository
import com.example.utils.AppConfig
import io.ktor.server.config.MapApplicationConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FactRepositoryTest {

    private lateinit var factRepository: FactRepository
    private lateinit var appConfig: AppConfig

    @BeforeEach
    fun setup() {
        factRepository = FactRepository()
        val mockConfig = MapApplicationConfig(
            "ktor.application.baseUrl" to "http://localhost:8080",
            "ktor.application.uselessFactApiUrl" to "https://uselessfacts.jsph.pl/random.json?language=en",
            "ktor.application.apiKey" to "test"
        )
        appConfig = AppConfig(mockConfig)
    }

    @Test
    fun `storeFact should store a fact with shortened URL and permalink`() {
        // given
        val shortenedUrl = "abcd1234"
        val fact = "This is a test fact"
        val permalink = "https://example.com/test"

        // when
        factRepository.storeFact(shortenedUrl, fact, permalink)

        // then
        val retrievedFact = factRepository.retrieveFact(shortenedUrl)
        assertEquals(FactDto(fact, permalink), retrievedFact)
    }

    @Test
    fun `retrieveFact should return null if shortened URL does not exist`() {
        //when
        val result = factRepository.retrieveFact("non-existent-url")
        assertNull(result)
    }

    @Test
    fun `retrieveAllFacts should return all stored facts`() {
        // given
        factRepository.storeFact("abcd1234", "Fact 1", "https://example.com/fact1")
        factRepository.storeFact("efgh5678", "Fact 2", "https://example.com/fact2")

        // when
        val allFacts = factRepository.retrieveAllFacts()

        // then
        assertEquals(2, allFacts.size)
        assertEquals(
            setOf(
                FactDto("Fact 1", "https://example.com/fact1"),
                FactDto("Fact 2", "https://example.com/fact2")
            ),
            allFacts.toSet()
        )
    }

    @Test
    fun `incrementAccessCount should increase access count for a given shortened URL`() {
        // given
        val shortenedUrl = "abcd1234"
        factRepository.storeFact(shortenedUrl, "Fact 1", "https://example.com/fact1")

        // when
        factRepository.incrementAccessCount(shortenedUrl)
        factRepository.incrementAccessCount(shortenedUrl)

        // then
        val statistics = factRepository.getAccessStatistics(appConfig)
        assertEquals(1, statistics.size)
        assertEquals(2, statistics[0].accessCount)
    }

    @Test
    fun `getAccessStatistics should return correct statistics`() {
        // given
        val baseUrl = "http://localhost:8080"
        val shortenedUrl1 = "abcd1234"
        val shortenedUrl2 = "efgh5678"

        factRepository.storeFact(shortenedUrl1, "Fact 1", "https://example.com/fact1")
        factRepository.storeFact(shortenedUrl2, "Fact 2", "https://example.com/fact2")
        factRepository.incrementAccessCount(shortenedUrl1)
        factRepository.incrementAccessCount(shortenedUrl2)
        factRepository.incrementAccessCount(shortenedUrl2)

        // when
        val statistics = factRepository.getAccessStatistics(appConfig)

        // then
        assertEquals(2, statistics.size)

        val expectedStatistics = setOf(
            AccessStatisticDto("$baseUrl/facts/$shortenedUrl1", 1),
            AccessStatisticDto("$baseUrl/facts/$shortenedUrl2", 2)
        )
        assertEquals(expectedStatistics, statistics.toSet())
    }
}