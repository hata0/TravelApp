package com.hata.travelapp.internal.di

import com.hata.travelapp.internal.api.google.directions.DirectionsApiService
import com.hata.travelapp.internal.data.repository.GoogleDirectionsRepositoryImpl
import com.hata.travelapp.internal.data.repository.FakeTripRepository
import com.hata.travelapp.internal.domain.trip.repository.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.service.RouteGenerator
import com.hata.travelapp.internal.domain.trip.service.RouteGeneratorImpl
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import com.hata.travelapp.internal.usecase.route.GenerateRouteUseCase
import com.hata.travelapp.internal.usecase.route.GenerateRouteUseCaseImpl
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.internal.usecase.trip.TripUsecaseImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * HiltのDIコンテナに、アプリケーション全体の依存性の解決方法を教えるモジュール。
 * SingletonComponentにインストールすることで、アプリケーションのライフサイクルで共有される。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // region API Clients
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideDirectionsApiService(retrofit: Retrofit): DirectionsApiService =
        retrofit.create(DirectionsApiService::class.java)
    // endregion

    // region Repositories
    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository = FakeTripRepository()

    @Provides
    @Singleton
    fun provideDirectionsRepository(
        apiService: DirectionsApiService
    ): DirectionsRepository = GoogleDirectionsRepositoryImpl(
        apiService = apiService,
        apiKey = "" // TODO: APIキーをBuildConfigから取得する
    )
    // endregion

    // region Domain Services
    @Provides
    @Singleton
    fun provideRouteGenerator(
        directionsRepository: DirectionsRepository
    ): RouteGenerator = RouteGeneratorImpl(directionsRepository)
    // endregion

    // region Use Cases
    @Provides
    @Singleton
    fun provideTripUsecase(
        tripRepository: TripRepository
    ): TripUsecase = TripUsecaseImpl(tripRepository)

    @Provides
    @Singleton
    fun provideGenerateRouteUseCase(
        tripRepository: TripRepository,
        routeGenerator: RouteGenerator
    ): GenerateRouteUseCase = GenerateRouteUseCaseImpl(tripRepository, routeGenerator)
    // endregion
}
