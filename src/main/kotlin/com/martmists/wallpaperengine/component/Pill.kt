package com.martmists.wallpaperengine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun Pill(
    text: String,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colorScheme.tertiaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = Modifier.padding(2.dp).background(
            color,
            shape,
        ).padding(8.dp),
    ) {
        Text(
            text,
            style = textStyle.copy(color = textColor),
        )
    }
}
