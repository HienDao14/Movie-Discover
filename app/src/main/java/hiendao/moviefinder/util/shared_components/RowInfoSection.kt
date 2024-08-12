package hiendao.moviefinder.util.shared_components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowInfoSection(
    modifier: Modifier = Modifier,
    label: String,
    info: String,
    infoColor: Color?
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, maxLines = 1, fontWeight = FontWeight.SemiBold)
        Text(
            text = info,
            fontSize = 14.sp,
            color = infoColor ?: MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}