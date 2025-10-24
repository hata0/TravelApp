package com.hata.travelapp.internal.config.app

import com.hata.travelapp.internal.config.server.ServerApiConfig

interface AppConfig {
    val serverApi: ServerApiConfig
}