package com.hata.travelapp.internal.config.server

import com.hata.travelapp.BuildConfig

class ServerApiEnvConfig private constructor(
    override val baseUrl: String
): ServerApiConfig{
    companion object {
        fun load(): ServerApiEnvConfig {
            return ServerApiEnvConfig(
                BuildConfig.SERVER_API_BASE_URL
            )
        }
    }
}
