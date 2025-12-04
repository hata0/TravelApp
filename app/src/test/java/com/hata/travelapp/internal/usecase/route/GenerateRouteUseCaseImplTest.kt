package com.hata.travelapp.internal.usecase.route

import org.junit.Test

/**
 * Usecase Layerの`GenerateRouteUseCaseImpl`のテスト。
 * このUsecaseが、依存するRepositoryやDomain Serviceを正しく呼び出し、
 * 結果を上位層に正しく伝達することを検証する。
 */
class GenerateRouteUseCaseImplTest {

    // TODO: @get:Rule を使って、MainCoroutineRuleをセットアップする

    // SUT (System Under Test) と、その依存対象のモック
    // TODO: lateinit var で、tripRepositoryとrouteGeneratorのモック、そしてusecaseを宣言する

    // TODO: @Before を使って、各テストの前にSUTとモックを初期化するセットアップメソッドを定義する

    @Test
    fun `execute - 正常系 - TripとDailyPlanが見つかる場合、RouteGeneratorが呼ばれRouteが返される`() {
        // Arrange (準備)
        // TODO: tripRepositoryモックが、テスト用のTripオブジェクトを返すように設定する
        // TODO: routeGeneratorモックが、テスト用のRouteオブジェクトを返すように設定する

        // Act (実行)
        // TODO: usecase.execute を呼び出す

        // Assert (表明)
        // TODO: 戻り値が、routeGeneratorが返したRouteオブジェクトと一致することを確認する
        // TODO: tripRepository.getById が1回だけ呼ばれたことを確認する (verify)
        // TODO: routeGenerator.generate が1回だけ呼ばれたことを確認する (verify)
    }

    @Test
    fun `execute - 異常系 - Tripが見つからない場合、nullが返される`() {
        // Arrange (準備)
        // TODO: tripRepositoryモックが、nullを返すように設定する

        // Act (実行)
        // TODO: usecase.execute を呼び出す

        // Assert (表明)
        // TODO: 戻り値がnullであることを確認する
        // TODO: routeGenerator.generate が一度も呼ばれなかったことを確認する (verify(exactly = 0))
    }
}
