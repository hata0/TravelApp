package com.hata.travelapp.internal.di

import android.content.Context
import androidx.room.Room
import com.hata.travelapp.internal.data.source.local.AppDatabase
import com.hata.travelapp.internal.data.source.local.dao.RouteLegDao
import com.hata.travelapp.internal.data.source.remote.DirectionsApiService
import com.hata.travelapp.internal.data.repository.GoogleDirectionsRepositoryImpl
import com.hata.travelapp.internal.data.repository.FakeTripRepository
import com.hata.travelapp.internal.domain.trip.repository.DirectionsRepository
import com.hata.travelapp.internal.domain.trip.service.TimelineGenerator
import com.hata.travelapp.internal.domain.trip.service.TimelineGeneratorImpl
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import com.hata.travelapp.internal.usecase.route.GenerateTimelineUseCase
import com.hata.travelapp.internal.usecase.route.GenerateTimelineUseCaseImpl
import com.hata.travelapp.internal.usecase.route.RecalculateTimelineUseCase
import com.hata.travelapp.internal.usecase.route.RecalculateTimelineUseCaseImpl
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.internal.usecase.trip.TripUsecaseImpl
import com.hata.travelapp.internal.usecase.trip.UpdateDailyStartTimeUseCase
import com.hata.travelapp.internal.usecase.trip.UpdateDailyStartTimeUseCaseImpl
import com.hata.travelapp.internal.usecase.trip.UpdateStayDurationUseCase
import com.hata.travelapp.internal.usecase.trip.UpdateStayDurationUseCaseImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    // region Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "travel-app-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRouteLegDao(appDatabase: AppDatabase): RouteLegDao {
        return appDatabase.routeLegDao()
    }
    // endregion

    // region Repositories
    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository = FakeTripRepository()

    @Provides
    @Singleton
    fun provideDirectionsRepository(
        apiService: DirectionsApiService,
        routeLegDao: RouteLegDao
    ): DirectionsRepository = GoogleDirectionsRepositoryImpl(
        apiService = apiService,
        routeLegDao = routeLegDao,
        apiKey = "" // TODO: APIキーをBuildConfigから取得する
    )
    // endregion

    // region Domain Services
    @Provides
    @Singleton
    fun provideTimelineGenerator(): TimelineGenerator = TimelineGeneratorImpl()
    // endregion

    // region Use Cases
    @Provides
    @Singleton
    fun provideTripUsecase(
        tripRepository: TripRepository
    ): TripUsecase = TripUsecaseImpl(tripRepository)

    @Provides
    @Singleton
    fun provideGenerateTimelineUseCase(
        tripRepository: TripRepository,
        directionsRepository: DirectionsRepository,
        timelineGenerator: TimelineGenerator
    ): GenerateTimelineUseCase = GenerateTimelineUseCaseImpl(
        tripRepository = tripRepository,
        directionsRepository = directionsRepository,
        timelineGenerator = timelineGenerator
    )

    @Provides
    @Singleton
    fun provideRecalculateTimelineUseCase(
        timelineGenerator: TimelineGenerator
    ): RecalculateTimelineUseCase = RecalculateTimelineUseCaseImpl(timelineGenerator)

    @Provides
    @Singleton
    fun provideUpdateDailyStartTimeUseCase(
        tripRepository: TripRepository
    ): UpdateDailyStartTimeUseCase = UpdateDailyStartTimeUseCaseImpl(tripRepository)

    @Provides
    @Singleton
    fun provideUpdateStayDurationUseCase(
        tripRepository: TripRepository
    ): UpdateStayDurationUseCase = UpdateStayDurationUseCaseImpl(tripRepository)
    // endregion
}
