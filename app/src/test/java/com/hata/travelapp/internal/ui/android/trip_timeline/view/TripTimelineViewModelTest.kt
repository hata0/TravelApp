package com.hata.travelapp.internal.ui.android.trip_timeline.view

import org.junit.Test

/**
 * ViewModel Layerの`TripTimelineViewModel`のテスト。
 * このViewModelが、Usecaseの呼び出しに応じて、UIの状態(StateFlow)を
 * 正しく更新することを検証する。
 */
class TripTimelineViewModelTest {

    // TODO: @get:Rule を使って、MainCoroutineRuleをセットアップする

    // SUT (System Under Test) と、その依存対象のモック
    // TODO: lateinit var で、generateRouteUseCaseのモックと、viewModelを宣言する

    // TODO: @Before を使って、各テストの前にSUTとモックを初期化するセットアップメソッドを定義する

    @Test
    fun `loadRoute - Usecaseが成功した場合、isLoadingはtrueからfalseに、routeは正しい値に更新される`() {
        // Arrange (準備)
        // TODO: generateRouteUseCaseモックが、テスト用のRouteオブジェクトを返すように設定する

        // Act (実行) & Assert (表明)
        // TODO: viewModel.route.test { ... } を使い、turbineでStateFlowの遷移を検証する
        // TODO: 初期状態がnullであることを確認する (awaitItem)
        // TODO: viewModel.loadRoute を呼び出す
        // TODO: 次の状態が、usecaseが返したRouteオブジェクトと一致することを確認する (awaitItem)

        // TODO: isLoadingのFlowも同様にテストする
    }

    @Test
    fun `loadRoute - Usecaseが失敗した場合、isLoadingはtrueからfalseに、routeはnullのまま`() {
        // Arrange (準備)
        // TODO: generateRouteUseCaseモックが、nullを返すように設定する

        // Act (実行) & Assert (表明)
        // TODO: viewModel.route.test { ... } を使ってStateFlowを検証する
        // TODO: 戻り値がnullのままであることを確認する
    }
}
