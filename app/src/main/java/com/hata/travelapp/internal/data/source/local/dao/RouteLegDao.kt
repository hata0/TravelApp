package com.hata.travelapp.internal.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hata.travelapp.internal.data.source.local.entity.RouteLegEntity

/**
 * `route_legs`テーブルへのアクセスを定義するData Access Object。
 */
@Dao
interface RouteLegDao {

    /**
     * 出発地と目的地のIDをキーに、キャッシュされた`RouteLegEntity`を取得する。
     * @param fromRoutePointId 出発地のID
     * @param toRoutePointId 目的地のID
     * @return 見つかった場合は`RouteLegEntity`、見つからなければnull
     */
    @Query("SELECT * FROM route_legs WHERE fromRoutePointId = :fromRoutePointId AND toRoutePointId = :toRoutePointId LIMIT 1")
    suspend fun getRouteLeg(fromRoutePointId: String, toRoutePointId: String): RouteLegEntity?

    /**
     * `RouteLegEntity`をデータベースに挿入する。既に存在する場合は上書きする。
     * @param routeLeg 保存するエンティティ
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteLeg(routeLeg: RouteLegEntity)
}
