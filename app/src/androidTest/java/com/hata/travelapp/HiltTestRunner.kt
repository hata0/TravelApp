package com.hata.travelapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Hiltを使ったインストルメンテーションテストを実行するためのカスタムTestRunner。
 * テスト実行時に、Hiltが管理するApplicationクラス(`HiltTestApplication`)を生成する。
 */
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
