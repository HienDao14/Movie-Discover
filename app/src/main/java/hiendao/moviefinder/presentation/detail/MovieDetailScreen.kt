package hiendao.moviefinder.presentation.detail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import hiendao.moviefinder.R
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.presentation.state.MovieDetailState
import hiendao.moviefinder.util.getAverageColor
import hiendao.moviefinder.util.getGenresFromCode
import hiendao.moviefinder.util.shared_components.RatingBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    movie: Movie,
    detailState: MovieDetailState,
    navHostController: NavHostController
) {
    val context = LocalContext.current

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        //function refresh
        Toast.makeText(context, "Refresh", Toast.LENGTH_SHORT).show()
        refreshing = false
    }

    val refreshState = rememberPullToRefreshState()


    val defaultDominantColor = MaterialTheme.colorScheme.primaryContainer
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    val imageUrl = makeFullUrl(movie.backdropPath)
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                refreshing, refreshState, onRefresh = {
                    refresh()
                }
            )

    ) {
        MovieDetailSection(movie = movie, modifier = modifier.fillMaxSize())

    }
}


@Composable
fun MovieDetailSection(
    modifier: Modifier = Modifier,
    movie: Movie
) {

    val pagerState = rememberPagerState(
        pageCount = { 5 }
    )
    val tabItems = listOf("Details", "Recommendations", "Credits", "Images", "Videos")

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            VideoTrailerSection(
                modifier = Modifier.fillMaxWidth(),
                imageUrl = makeFullUrl(movie.backdropPath)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 150.dp, start = 15.dp, end = 15.dp)
                    .wrapContentHeight()
            ) {
                ImageSection(movie = movie, modifier = Modifier.padding(start = 10.dp))

                Spacer(modifier = Modifier.width(20.dp))

                MovieInfoSection(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(top = 50.dp),
                    movie = movie
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        //Done basic info

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { position ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(position[pagerState.currentPage]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            tabItems.forEachIndexed { index, label ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            selectedTabIndex = index
                            pagerState.scrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = label, maxLines = 1)
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) { page ->
            when (page) {
                0 -> {
                    Column {
                        OverviewSection(movie = movie)
                    }
                }

                1 -> {

                }

                2 -> {}
                else -> {}
            }
        }

    }
}

@Preview
@Composable
fun PreviewDetailScreen() {
    val movie = Movie(
        id = 1,
        adult = false,
        backdropPath = "",
        genreIds = listOf(28, 12, 878),
        originalLanguage = "en",
        originalTitle = "Furiosa: A Mad Max Saga",
        overview = "As the world fell, young Furiosa is snatched from the Green Place of Many Mothers and falls into the hands of a great Biker Horde led by the Warlord Dementus. Sweeping through the Wasteland they come across the Citadel presided over by The Immortan Joe. While the two Tyrants war for dominance, Furiosa must survive many trials as she puts together the means to find her way home.",
        popularity = 6058.314,
        posterPath = "/iADOJ8Zymht2JPMoy3R7xceZprc.jpg",
        releaseDate = "2024-05-22",
        title = "Furiosa: A Mad Max Saga",
        video = false,
        voteAverage = 7.712,
        voteCount = 1605,
        budget = 200000000,
        homepage = "https://movies.disney.com/inside-out-2",
        revenue = 1014,
        runtime = 97,
        status = "Released",
        tagline = "Make room for new emotions.",
        productionCompany = listOf("Walt Disney Pictures", "Pixar"),
        originCountry = listOf("United States of America"),
        similar = listOf(387033, 809),
        images = listOf("/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg", "/9h2KgGXSmWigNTn3kQdEFFngj9i.jpg"),
        videos = listOf("RY5aH21ohU4", "eioXDOSx6rQ", "L4DrolmDxmw"),
        collectionId = 1022790
    )

    var overviewExpanded by remember {
        mutableStateOf(false)
    }

    MovieDetailSection(modifier = Modifier.fillMaxSize(), movie = movie)
}

@Composable
fun OverviewSection(
    modifier: Modifier = Modifier,
    movie: Movie
) {
    var overviewExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.padding(top = 8.dp)
    ) {

        /*
        * releaseDate = "2024-05-22",
        * voteAverage = 7.712,
        voteCount = 1605,
        *  budget = 200000000, revenue = 1014, runtime = 97,
        status = "Released",
        * productionCompany = listOf("Walt Disney Pictures", "Pixar"),*/



        if (movie.tagline != "") {
            Text(
                text = "\"${movie.tagline}\"",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(text = "Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        if (overviewExpanded) {
            Text(
                text = movie.overview,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.clickable {
                    overviewExpanded = false
                }
            )

        } else {

            Text(
                text = movie.overview,
                fontSize = 16.sp,
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

@Composable
fun ImageSection(
    modifier: Modifier = Modifier,
    movie: Movie
) {
    val context = LocalContext.current

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(makeFullUrl(movie.posterPath))
            .size(Size.ORIGINAL)
            .build()
    )

    val imageState = imagePainter.state

    Card(
        modifier = modifier
            .size(width = 140.dp, height = 250.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (imageState) {
                is AsyncImagePainter.State.Success -> {
                    Image(
                        bitmap = imageState.result.drawable.toBitmap().asImageBitmap(),
                        contentDescription = "Trailer Video",
                        contentScale = ContentScale.Crop
                    )
                }

                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center)
                            .scale(0.5f)
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "No image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(32.dp)
                            .alpha(0.8f),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun VideoTrailerSection(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    val context = LocalContext.current

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )

    val imageState = imagePainter.state

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        when (imageState) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    bitmap = imageState.result.drawable.toBitmap().asImageBitmap(),
                    contentDescription = "Trailer Video",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            val colors = listOf(
                                Color.Black,
                                Color.Transparent
                            )
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(colors),
                                blendMode = BlendMode.DstIn
                            )
                        }
                )
            }

            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.Center)
                        .scale(0.5f)
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "No image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(32.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun MovieInfoSection(
    modifier: Modifier = Modifier,
    movie: Movie
) {
    val genres = getGenresFromCode(movie.genreIds).joinToString(" - ") { it.name }

    val runtimeText = if (movie.runtime > 60) "${movie.runtime / 60} hr ${movie.runtime % 60} min"
    else "${movie.runtime} min"

    Column(
        modifier = modifier
    ) {
        Text(text = movie.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RatingBar(
                starsModifier = Modifier.size(14.dp),
                rating = movie.voteAverage / 2
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = movie.voteAverage.toString().take(3),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = if (movie.adult) "+18" else "-13",
            fontSize = 14.sp,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = genres,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = runtimeText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
