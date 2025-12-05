package com.hata.travelapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * HiltのエントリーポイントとなるカスタムApplicationクラス。
 * このアノテーションにより、アプリケーション全体で依存性の注入が可能になる。
 */
@HiltAndroidApp
class TravelApplication : Application() {
}
