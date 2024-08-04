package hiendao.moviefinder.presentation.movieDetail

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.presentation.movieDetail.credit.CreditSection
import hiendao.moviefinder.presentation.movieDetail.images.ImageSection
import hiendao.moviefinder.presentation.movieDetail.overview.OverviewSection
import hiendao.moviefinder.presentation.movieDetail.recommendation.RecommendationSection
import hiendao.moviefinder.presentation.state.FavoriteState
import hiendao.moviefinder.presentation.state.MovieDetailState
import hiendao.moviefinder.presentation.uiEvent.MovieDetailEvent
import hiendao.moviefinder.util.Constant.MaxToolbarHeight
import hiendao.moviefinder.util.Constant.MinToolbarHeight
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.appBarState.CustomAppBar
import hiendao.moviefinder.util.appBarState.ExitUntilCollapsedState
import hiendao.moviefinder.util.appBarState.ToolbarState
import hiendao.moviefinder.util.convert.getGenresFromCode
import hiendao.moviefinder.util.getAverageColor
import hiendao.moviefinder.util.shared_components.CustomImage
import hiendao.moviefinder.util.shared_components.ImageScreen
import hiendao.moviefinder.util.shared_components.RatingBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
private fun rememberToolbarState(toolbarHeightRange: IntRange): ToolbarState {
    return rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
        ExitUntilCollapsedState(toolbarHeightRange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    movie: Movie,
    detailState: MovieDetailState,
    navHostController: NavHostController,
    onEvent: (MovieDetailEvent) -> Unit
) {
    var showImageScreen by remember {
        mutableStateOf(false)
    }
    var imageUriToShow by remember {
        mutableStateOf("")
    }
    var imageTitleToShow by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(MovieDetailEvent.Refresh(type = "Detail", movieId = movie.id))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    val imageUrl = makeFullUrl(movie.backdropPath)
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )

    val toolbarHeightRange = with(LocalDensity.current) {
        MinToolbarHeight.roundToPx()..MaxToolbarHeight.roundToPx()
    }
    val toolbarState = rememberToolbarState(toolbarHeightRange)
    val scrollState = rememberScrollState()

    toolbarState.scrollValue = scrollState.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(refreshState.nestedScrollConnection)
    ) {
        if (showImageScreen && imageUriToShow.isNotEmpty()) {
            ImageScreen(
                uri = imageUriToShow,
                title = imageTitleToShow,
                setShowImage = {
                    showImageScreen = it
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        CustomAppBar(
            imagePainter = imagePainter,
            progress = toolbarState.progress,
            title = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                .graphicsLayer { translationY = toolbarState.offset },
            limit = toolbarState.progress != 0f,
            onIconClick = {
                navHostController.navigateUp()
            }
        )

        MovieDetailSection(
            movie = movie,
            modifier = modifier.fillMaxSize(),
            scrollState = scrollState,
            detailState = detailState,
            movieItemClick = { movieId ->
                navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movieId}")
            },
            creditItemClick = { creditId ->
                navHostController.navigate("${NavRoute.CREDIT_SCREEN}?creditId=${creditId}")
            },
            showImage = { uri, title ->
                showImageScreen = true
                imageUriToShow = uri
                imageTitleToShow = title
            },
            changeFavoriteClick = {favorite, date, movieId ->
                onEvent(MovieDetailEvent.AddToFavorite(favorite, date, movieId))
            }
        )

        CustomAppBar(
            imagePainter = imagePainter,
            progress = toolbarState.progress,
            title = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                .graphicsLayer { translationY = toolbarState.offset },
            limit = toolbarState.progress == 0f,
            onIconClick = {
                navHostController.navigateUp()
            }
        )

        if (refreshState.isRefreshing) {
            LaunchedEffect(true) {
                refresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                refreshState.startRefresh()
            } else {
                refreshState.endRefresh()
            }
        }

        PullToRefreshContainer(state = refreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}


@Composable
fun MovieDetailSection(
    modifier: Modifier = Modifier,
    movie: Movie,
    scrollState: ScrollState,
    detailState: MovieDetailState,
    movieItemClick: (Int) -> Unit,
    creditItemClick: (Int) -> Unit,
    changeFavoriteClick: (Int, String, Int) -> Unit,
    showImage: (String, String) -> Unit
) {

    val pagerState = rememberPagerState(
        pageCount = { 5 }
    )
    val tabItems = listOf("Details", "Recommendations", "Credits", "Images", "Videos")

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .verticalScroll(scrollState)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, start = 15.dp, end = 15.dp)
                .wrapContentHeight()
        ) {
            CustomImage(
                imageUrl = makeFullUrl(movie.posterPath),
                modifier = Modifier.padding(start = 10.dp),
                width = 120.dp,
                height = 220.dp,
                onClick = {
                    showImage(movie.posterPath, "${movie.title} Poster")
                }
            )

            Spacer(modifier = Modifier.width(20.dp))

            MovieInfoSection(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 50.dp),
                movie = movie,
                changeFavoriteClick
            )
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
                        OverviewSection(movie = movie, modifier = Modifier.fillMaxWidth())
                    }
                }

                1 -> {
                    RecommendationSection(
                        movie = movie,
                        collections = detailState.collectionVideos,
                        similar = detailState.similarVideos,
                        navigate = {
                            movieItemClick(it)
                        }
                    )
                }

                2 -> {
                    if (detailState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(50.dp)
                            )
                        }
                    }
                    if (detailState.listCredit.isNotEmpty()) {
                        CreditSection(
                            movieId = movie.id,
                            credits = detailState.listCredit,
                            navigate = {
                                creditItemClick(it)
                            }
                        )
                    }
                }

                3 -> {
                    ImageSection(
                        images = movie.images,
                        showImage = { uri, title ->
                            showImage(uri, "\"${movie.title}\" $title")
                        }
                    )
                }

                4 -> {}
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


    MovieDetailSection(
        modifier = Modifier.fillMaxSize(),
        movie = movie,
        scrollState = rememberScrollState(),
        detailState = MovieDetailState(),
        movieItemClick = {},
        creditItemClick = {},
        showImage = { _, _ ->

        },
        changeFavoriteClick = {_, _, _ ->

        }
    )
}


@Composable
fun MovieInfoSection(
    modifier: Modifier = Modifier,
    movie: Movie,
    changeFavoriteClick: (Int, String, Int) -> Unit
) {
    val genres = getGenresFromCode(movie.genreIds).joinToString(" - ") { it.name }

    val runtimeText = if (movie.runtime > 60) "${movie.runtime / 60} hr ${movie.runtime % 60} min"
    else "${movie.runtime} min"

    val addedToFavorite = rememberSaveable {
        mutableStateOf(movie.addedInFavorite)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        Text(text = movie.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = if (movie.adult) "+18" else "-13",
            fontSize = 12.sp,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = genres,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = runtimeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(
                        starsModifier = Modifier.size(12.dp),
                        rating = movie.voteAverage / 2
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = movie.voteAverage.toString().take(3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Button(
                onClick = {
                    val favorite = if(addedToFavorite.value) 0 else 1
                    val dateTime = LocalDateTime.now()
                    changeFavoriteClick(favorite, dateTime.toString(), movie.id)
                    addedToFavorite.value = !addedToFavorite.value
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                if (addedToFavorite.value) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Added to favorite"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorite"
                    )
                }
            }
        }
    }
}