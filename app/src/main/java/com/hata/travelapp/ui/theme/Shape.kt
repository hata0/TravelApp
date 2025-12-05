package com.hata.travelapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ポップでユニークな形状設定

val Shapes = Shapes(
    // TagやChipなど
    extraSmall = RoundedCornerShape(50),

    // Buttonなど: シンプルなカプセル型
    small = RoundedCornerShape(50),

    // Cardなど: 普通の角が丸い長方形
    medium = RoundedCornerShape(16.dp),

    // FAB(作成ボタン、切り替えボタン)など: 葉っぱ型
    // 対角線上の角を大きく丸め、残りを少し鋭角にすることで動きを出します。
    large = RoundedCornerShape(
        topStart = 28.dp,
        bottomEnd = 28.dp,
        topEnd = 4.dp,
        bottomStart = 4.dp
    ),

    // BottomSheetなど
    extraLarge = RoundedCornerShape(
        topStart = 48.dp,
        topEnd = 48.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
)
