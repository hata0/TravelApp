package com.hata.travelapp.internal.data.repository

import org.junit.Test

/**
 * Data Layerの`GoogleDirectionsRepositoryImpl`のテスト。
 * 偽物のAPIサーバー(MockWebServer)を使い、ネットワークレスポンスを
 * ドメインモデルへ正しくマッピングできるかを検証する。
 */
class GoogleDirectionsRepositoryImplTest {

    // TODO: @get:Rule を使って、MainCoroutineRuleをセットアップする

    // TODO: MockWebServerのインスタンスを宣言する
    // private lateinit var server: MockWebServer

    // TODO: SUT (System Under Test)を宣言する
    // private lateinit var repository: GoogleDirectionsRepositoryImpl

    // TODO: @Before を使って、各テストの前にサーバーを起動し、SUTを初期化するセットアップメソッドを定義する
    // TODO: RetrofitのbaseUrlを、起動したMockWebServerのURLに向ける

    // TODO: @After を使って、各テストの後にサーバーをシャットダウンするメソッドを定義する

    @Test
    fun `getDirections - APIが成功レスポンスを返した場合、RouteLegに正しくマッピングされる`() {
        // Arrange (準備)
        // TODO: 偽物のAPIサーバーに、成功時のJSONレスポンスをセットする (server.enqueue)
        // TODO: JSONファイルは `app/src/test/resources` に置くのが一般的

        // Act (実行)
        // TODO: repository.getDirections を呼び出す

        // Assert (表明)
        // TODO: 戻り値の`RouteLeg`がnullでないことを確認する
        // TODO: duration, polyline, stepsの各プロパティが、JSONの内容と一致しているか検証する
    }

    @Test
    fun `getDirections - APIがエラーレスポンスを返した場合、nullが返される`() {
        // Arrange (準備)
        // TODO: 偽物のAPIサーバーに、エラーステータスコード(404, 500など)をセットする

        // Act (実行)
        // TODO: repository.getDirections を呼び出す

        // Assert (表明)
        // TODO: 戻り値がnullであることを確認する
    }
}
