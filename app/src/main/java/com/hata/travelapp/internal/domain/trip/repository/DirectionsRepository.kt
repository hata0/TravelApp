package com.hata.travelapp.internal.domain.trip.repository

import com.hata.travelapp.internal.domain.trip.entity.Destination
import com.hata.travelapp.internal.domain.trip.entity.RouteLeg

/**
 * 2つの地点間のルート情報を、外部サービスに問い合わせる責務を持つRepositoryのインターフェース。
 * このインターフェースはドメイン層に属し、具体的な技術（Google API, Retrofitなど）を一切知らない。
 */
interface DirectionsRepository {

    /**
     * 出発地と目的地の`Destination`オブジェクトを受け取り、その間の移動区間情報を`RouteLeg`として返す。
     * ルートが見つからない場合やエラーが発生した場合は、nullを返すことを期待する。
     * @param from 出発地のDestinationオブジェクト
     * @param to 目的地のDestinationオブジェクト
     * @return 計算された移動区間情報（`RouteLeg`）、またはnull
     */
    suspend fun getDirections(from: Destination, to: Destination): RouteLeg?
}