package com.example.services

import com.example.dtos.AccessStatisticDto
import com.example.dtos.FactDto
import com.example.dtos.ShortenedUrlDto
import com.example.dtos.UselessFactResponseDto
import com.example.repository.FactRepository
import com.example.utils.AppConfig
import com.example.utils.appHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

class FactService(
    private val appConfig: AppConfig,
    private val httpClient: HttpClient = appHttpClient,
    private val factRepository: FactRepository = FactRepository()
) {

    private val log = LoggerFactory.getLogger(FactService::class.java)

    suspend fun fetchAndShortenFacts(): ShortenedUrlDto {
        val uselessFact = fetchUselessFact()
        val shortenedUrl = generateShortenedUrl()
        factRepository.storeFact(shortenedUrl, uselessFact.text, uselessFact.permalink)
        return ShortenedUrlDto(
            originalFact = uselessFact.text,
            shortenedUrl = buildShortenedUrl(shortenedUrl)
        )
    }

    suspend fun getFact(shortenedUrl: String): FactDto? {
        val fact = factRepository.retrieveFact(shortenedUrl)
        return if (fact != null) {
            factRepository.incrementAccessCount(shortenedUrl)
            fact
        } else null
    }

    suspend fun getAllFacts(): List<FactDto> {
        return factRepository.retrieveAllFacts()
    }

    suspend fun getOriginalPermalink(shortenedUrl: String): String? {
        val permalink = factRepository.retrievePermalink(shortenedUrl)
        if (permalink != null) {
            factRepository.incrementAccessCount(shortenedUrl)
        }
        return permalink
    }

    suspend fun getAccessStatistics(): List<AccessStatisticDto> {
        return factRepository.getAccessStatistics(appConfig)
    }

    private suspend fun fetchUselessFact(): UselessFactResponseDto {
        return try {
            httpClient.get(appConfig.uselessFactApiUrl).body()
        } catch (e: Exception) {
            log.error("Error while fetching fact from API: ${e.message}")
            throw e
        }
    }

    private fun generateShortenedUrl(): String {
        return java.util.UUID.randomUUID().toString().take(8)
    }

    private fun buildShortenedUrl(shortenedUrl: String): String {
        return "${appConfig.baseUrl}/facts/$shortenedUrl"
    }
}