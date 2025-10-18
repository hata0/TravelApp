package com.hata.travelapp.internal.config.app

import com.hata.travelapp.internal.config.server.ServerApiConfig
import com.hata.travelapp.internal.config.server.ServerApiEnvConfig

class AppEnvConfig private constructor(
    override val serverApi: ServerApiConfig
) : AppConfig{
    companion object {
        fun load(): AppEnvConfig {
            return AppEnvConfig(
                ServerApiEnvConfig.load()
            )
        }
    }
}