package com.hata.travelapp.internal.config.server

import com.hata.travelapp.BuildConfig

data class ServerApiEnvConfig(
    override val baseUrl: String = BuildConfig.SERVER_API_BASE_URL
) : ServerApiConfig
