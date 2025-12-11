package com.hata.travelapp.internal.di

import android.content.Context
import androidx.room.Room
import com.hata.travelapp.internal.data.repository.FakeRoutesRepository
import com.hata.travelapp.internal.data.repository.FakeTripRepository
import com.hata.travelapp.internal.data.repository.GoogleRoutesRepositoryImpl
import com.hata.travelapp.internal.data.source.local.AppDatabase
import com.hata.travelapp.internal.data.source.local.dao.RouteLegDao
import com.hata.travelapp.internal.data.source.local.dao.TripDao
import com.hata.travelapp.internal.data.source.remote.RoutesApiService
import com.hata.travelapp.internal.domain.trip.repository.RoutesRepository
import com.hata.travelapp.internal.domain.trip.repository.TripRepository
import com.hata.travelapp.internal.domain.trip.service.TimelineGenerator
import com.hata.travelapp.internal.domain.trip.service.TimelineGeneratorImpl
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.GenerateTimelineUseCaseImpl
import com.hata.travelapp.internal.usecase.trip.RecalculateTimelineUseCase
import com.hata.travelapp.internal.usecase.trip.RecalculateTimelineUseCaseImpl
import com.hata.travelapp.internal.usecase.trip.TripUsecase
import com.hata.travelapp.internal.usecase.trip.TripUsecaseImpl
import com.hata.travelapp.internal.usecase.trip.UpdateDailyPlanUseCase
import com.hata.travelapp.internal.usecase.trip.UpdateDailyPlanUseCaseImpl
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
    fun provideRoutesApiService(retrofit: Retrofit): RoutesApiService =
        retrofit.create(RoutesApiService::class.java)
    // endregion

    // region Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "travel-app-database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideRouteLegDao(appDatabase: AppDatabase): RouteLegDao {
        return appDatabase.routeLegDao()
    }

    @Provides
    @Singleton
    fun provideTripDao(appDatabase: AppDatabase): TripDao {
        return appDatabase.tripDao()
    }
    // endregion

    // region Repositories
    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository = FakeTripRepository() // Swapped to FakeTripRepository

    @Provides
    @Singleton
    fun provideRoutesRepository(): RoutesRepository = FakeRoutesRepository()
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
        routesRepository: RoutesRepository,
        timelineGenerator: TimelineGenerator
    ): GenerateTimelineUseCase = GenerateTimelineUseCaseImpl(
        tripRepository = tripRepository,
        routesRepository = routesRepository,
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

    @Provides
    @Singleton
    fun provideUpdateDailyPlanUseCase(
        tripRepository: TripRepository
    ): UpdateDailyPlanUseCase = UpdateDailyPlanUseCaseImpl(tripRepository)
    // endregion
}
