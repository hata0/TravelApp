package com.hata.travelapp.internal.domain.directions

import com.hata.travelapp.internal.domain.trip.Destination
import com.hata.travelapp.internal.domain.trip.Transportation

/**
 * 2つの地点間のルート情報を、外部サービスに問い合わせる責務を持つRepositoryのインターフェース。
 * このインターフェースはドメイン層に属し、具体的な技術（Google API, Retrofitなど）を一切知らない。
 */
interface DirectionsRepository {

    /**
     * 出発地と目的地の`Destination`オブジェクトを受け取り、その間の移動ステップ（`Transportation`）のリストを返す。
     * ルートが見つからない場合やエラーが発生した場合は、空のリストを返すことを期待する。
     * @param from 出発地のDestinationオブジェクト
     * @param to 目的地のDestinationオブジェクト
     * @return 計算された移動ステップ（Transportation）のリスト
     */
    suspend fun getDirections(from: Destination, to: Destination): List<Transportation>
}
