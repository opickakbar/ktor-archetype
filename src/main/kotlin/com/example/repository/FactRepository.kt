package com.example.repository

import com.example.dtos.AccessStatisticDto
import com.example.dtos.FactDto
import com.example.utils.AppConfig
import java.util.concurrent.ConcurrentHashMap

class FactRepository {
    private val factStorage = ConcurrentHashMap<String, String>()
    private val linkStorage = ConcurrentHashMap<String, String>()
    private val accessCounts = ConcurrentHashMap<String, Int>()

    fun storeFact(shortenedUrl: String, fact: String, permalink: String) {
        factStorage[shortenedUrl] = fact
        linkStorage[shortenedUrl] = permalink
        accessCounts[shortenedUrl] = 0
    }

    fun retrieveFact(shortenedUrl: String): FactDto? {
        val fact = factStorage[shortenedUrl]
        val permalink = linkStorage[shortenedUrl]
        return if (fact != null && permalink != null) {
            FactDto(fact, permalink)
        } else null
    }

    fun retrieveAllFacts(): List<FactDto> {
        return factStorage.keys.mapNotNull { shortenedUrl ->
            retrieveFact(shortenedUrl)
        }
    }

    fun retrievePermalink(shortenedUrl: String): String? {
        return linkStorage[shortenedUrl]
    }

    fun incrementAccessCount(shortenedUrl: String) {
        accessCounts.merge(shortenedUrl, 1, Int::plus)
    }

    fun getAccessStatistics(appConfig: AppConfig): List<AccessStatisticDto> {
        return accessCounts.mapNotNull { (shortenedUrl, count) ->
            AccessStatisticDto(
                shortenedUrl = "${appConfig.baseUrl}/facts/$shortenedUrl",
                accessCount = count)
        }
    }

    // for testing purpose
    fun clear() {
        factStorage.clear()
        linkStorage.clear()
        accessCounts.clear()
    }
}