package com.hata.travelapp.internal.config.app

import com.hata.travelapp.internal.config.server.ServerApiConfig
import com.hata.travelapp.internal.config.server.ServerApiEnvConfig

data class AppEnvConfig(
    override val serverApi: ServerApiConfig = ServerApiEnvConfig()
) : AppConfig