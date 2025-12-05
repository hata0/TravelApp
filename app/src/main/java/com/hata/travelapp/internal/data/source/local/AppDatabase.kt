package com.hata.travelapp.internal.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hata.travelapp.internal.data.source.local.converter.DatabaseConverters
import com.hata.travelapp.internal.data.source.local.dao.RouteLegDao
import com.hata.travelapp.internal.data.source.local.dao.TripDao
import com.hata.travelapp.internal.data.source.local.entity.RouteLegEntity
import com.hata.travelapp.internal.data.source.local.entity.TripEntity

/**
 * アプリケーション全体のRoomデータベースを定義するクラス。
 * このデータベースが管理するエンティティ(テーブル)と型コンバーターを宣言する。
 */
@Database(
    entities = [RouteLegEntity::class, TripEntity::class], // TripEntityを追加
    version = 2, // スキーマ変更のためバージョンを上げる
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * `RouteLegDao`のインスタンスを提供する抽象メソッド。
     * Roomがこのメソッドの実装を自動生成する。
     */
    abstract fun routeLegDao(): RouteLegDao

    /**
     * `TripDao`のインスタンスを提供する抽象メソッド。
     */
    abstract fun tripDao(): TripDao
}
