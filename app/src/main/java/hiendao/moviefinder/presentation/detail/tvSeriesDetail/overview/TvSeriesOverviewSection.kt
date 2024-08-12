package hiendao.moviefinder.presentation.detail.tvSeriesDetail.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.util.convert.ConvertToMillion
import hiendao.moviefinder.util.shared_components.RowInfoSection

@Composable
fun TvSeriesOverviewSection(
    modifier: Modifier = Modifier,
    series: TvSeries
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
            info = "${series.voteAverage} / 10",
            infoColor = MaterialTheme.colorScheme.primary
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Status",
            info = series.status,
            infoColor = if (series.status == "Released") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "First Air Date",
            info = series.firstAirDate,
            null
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Last Air Date",
            info = series.lastAirDate,
            null
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Number of Seasons",
            info = series.numberOfSeasons.toString(),
            null
        )

        RowInfoSection(
            modifier = Modifier.fillMaxWidth(),
            label = "Number of episodes",
            info = series.numberOfEpisodes.toString(),
            null
        )

        if (series.tagline != "") {
            Text(
                text = "\"${series.tagline}\"",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 20.dp),
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
                            text = series.overview,
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
                },
                onTextLayout = {

                }
            )

        } else {

            Text(
                text = buildAnnotatedString {
                    append(
                        AnnotatedString(
                            text = series.overview,
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

    }

}