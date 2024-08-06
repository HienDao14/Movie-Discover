package hiendao.moviefinder.util.shared_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.util.NavRoute

@Composable
fun AutoSwipeSection(
    modifier: Modifier = Modifier,
    sectionType: String,
    listMedia: List<Media>,
    navHostController: NavHostController
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = sectionType,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        AutoSwipePager(
            movies = listMedia.take(7),
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),
            navigate = {
                navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${it}")
            }
        )
    }
}