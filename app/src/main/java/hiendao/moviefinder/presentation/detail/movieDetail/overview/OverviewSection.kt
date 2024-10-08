package hiendao.moviefinder.presentation.detail.movieDetail.overview

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.convert.ConvertToMillion
import hiendao.moviefinder.util.shared_components.RowInfoSection


@Composable
fun OverviewSection(
    modifier: Modifier = Modifier,
    movie: Movie,
    navigate: (String) -> Unit
) {
    var overviewExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "User score",
            info = "${movie.voteAverage} / 10",
            infoColor = MaterialTheme.colorScheme.primary
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Status",
            info = movie.status,
            infoColor = if (movie.status == "Released") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Release Date",
            info = movie.releaseDate,
            null
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Budget",
            info = movie.budget.toLong().ConvertToMillion(),
            null
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Revenue",
            info = movie.revenue.ConvertToMillion(),
            if (movie.revenue > movie.budget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )

        if (movie.tagline != "") {
            Text(
                text = "\"${movie.tagline}\"",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Center

            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(text = "Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        if (overviewExpanded) {
            Text(
                text = buildAnnotatedString {
                    append(
                        AnnotatedString(
                            text = movie.overview,
                            paragraphStyle = ParagraphStyle(
                                textIndent = TextIndent(firstLine = 20.sp, restLine = 0.sp)
                            )
                        )
                    )
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.clickable {
                    overviewExpanded = false
                }
            )

        } else {

            Text(
                text = buildAnnotatedString {
                    append(
                        AnnotatedString(
                            text = movie.overview,
                            paragraphStyle = ParagraphStyle(
                                textIndent = TextIndent(firstLine = 20.sp, restLine = 0.sp)
                            )
                        )
                    )
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable {
                    overviewExpanded = true
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Visit homepage: ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = movie.homepage,
                textDecoration = TextDecoration.Underline,
                color = Color.Blue,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        navigate(movie.homepage)
                    }
                    .basicMarquee(
                        delayMillis = 2_000
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
