package com.hata.travelapp.internal.usecase.route

import com.hata.travelapp.internal.domain.route.Route
import com.hata.travelapp.internal.domain.route.RouteGenerator
import com.hata.travelapp.internal.domain.trip.TripId
import com.hata.travelapp.internal.domain.trip.TripRepository

/**
 * Tripの情報を元に、UIが表示すべき形式である`Route`オブジェクトを生成する責務を持つUsecase。
 */
interface GenerateRouteUseCase {
    suspend fun execute(tripId: TripId): Route?
}

/**
 * `GenerateRouteUseCase`の実装クラス。
 * 実際のロジックはドメイン層の`RouteGenerator`に委譲し、自身は調整役の責務に徹する。
 */
class GenerateRouteUseCaseImpl(
    private val tripRepository: TripRepository,
    private val routeGenerator: RouteGenerator // DirectionsRepositoryの代わりにRouteGeneratorを受け取る
) : GenerateRouteUseCase {

    override suspend fun execute(tripId: TripId): Route? {
        // 1. Repositoryからデータを取得する
        val trip = tripRepository.getById(tripId) ?: return null

        // 2. ドメインサービスにビジネスロジックの実行を委譲する
        return routeGenerator.generate(trip)
    }
}
