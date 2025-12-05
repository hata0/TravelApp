package com.hata.travelapp

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Hiltを使ったComposeのテストで使用するための、空のActivity。
 * `@AndroidEntryPoint`アノテーションが付いているため、Hiltによる依存性注入をサポートする。
 * 自身の`onCreate`で`setContent`を呼ばないため、テストルール側でUIを自由に設定できる。
 */
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()
