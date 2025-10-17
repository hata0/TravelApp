package com.hata.travelapp.internal.presentation.android.trip.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DateSelectionScreen(
    onNavigateToMap: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    // TODO: Replace with actual data from ViewModel
    val dates = listOf("2024/08/01 (木)", "2024/08/02 (金)", "2024/08/03 (土)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("日程を選択")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(dates) {
                date ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToMap(date) }
                ) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("戻る")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateSelectionScreenPreview() {
    DateSelectionScreen(onNavigateToMap = {}, onNavigateBack = {})
}
