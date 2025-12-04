package com.hata.travelapp.internal.domain.trip.service

import org.junit.Test

/**
 * Domain Layerの`RouteGeneratorImpl`の単体テスト。
 * このテストは、純粋なビジネスロジックが、外部の依存関係（Repository）から独立して
 * 正しく動作することを保証する。
 */
class RouteGeneratorImplTest {

    // TODO: @get:Rule を使って、MainCoroutineRuleをセットアップする

    // SUT (System Under Test) と、その依存対象のモック
    // TODO: lateinit var で、directionsRepositoryのモックと、routeGeneratorを宣言する

    // TODO: @Before を使って、各テストの前にSUTとモックを初期化するセットアップメソッドを定義する

    @Test
    fun `generate - 目的地が複数の場合、到着時刻と出発時刻が正しく計算される`() {
        // Arrange (準備)
        // TODO: directionsRepositoryモックの動作を定義する (every { ... } returns ...)
        // TODO: テストデータとして、複数の目的地を持つリストを作成する

        // Act (実行)
        // TODO: routeGenerator.generate を呼び出す

        // Assert (表明)
        // TODO: 戻り値の`Route`オブジェクトの中身を検証する (assertEquals)
        // TODO: 2番目の目的地の到着時刻は、1番目の出発時刻 + 移動時間になっているか？
        // TODO: 3番目の目的地の到着時刻は、2番目の出発時刻 + 移動時間になっているか？
    }

    @Test
    fun `generate - 目的地が空の場合、空のルートが返される`() {
        // Arrange (準備)
        // TODO: 空の目的地リストを作成する

        // Act (実行)
        // TODO: routeGenerator.generate を呼び出す

        // Assert (表明)
        // TODO: 戻り値の`Route`が、空のstopsとlegsを持っていることを検証する
    }
}
