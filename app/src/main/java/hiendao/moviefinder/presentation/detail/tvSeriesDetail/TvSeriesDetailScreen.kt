package hiendao.moviefinder.presentation.detail.tvSeriesDetail

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.presentation.detail.creditSection.CreditSection
import hiendao.moviefinder.presentation.detail.imageSection.ImageSection
import hiendao.moviefinder.presentation.detail.recommendationSection.RecommendationSection
import hiendao.moviefinder.presentation.state.SeriesDetailState
import hiendao.moviefinder.presentation.detail.tvSeriesDetail.overview.TvSeriesOverviewSection
import hiendao.moviefinder.presentation.detail.videoSection.VideoSection
import hiendao.moviefinder.presentation.uiEvent.SeriesDetailEvent
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.appBarState.CustomAppBar
import hiendao.moviefinder.util.appBarState.ExitUntilCollapsedState
import hiendao.moviefinder.util.appBarState.ToolbarState
import hiendao.moviefinder.util.convert.convertDateFormat
import hiendao.moviefinder.util.convert.getGenresFromCode
import hiendao.moviefinder.util.shared_components.CustomImage
import hiendao.moviefinder.util.shared_components.ImagesScreen
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
fun TvSeriesDetailScreen(
    modifier: Modifier = Modifier,
    series: TvSeries,
    seriesDetailState: SeriesDetailState,
    navHostController: NavHostController,
    onEvent: (SeriesDetailEvent) -> Unit
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

    var images = remember {
        mutableStateListOf<String>()
    }

    var isPoster by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(SeriesDetailEvent.Refresh(type = "SeriesDetail", seriesId = series.id))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()


    val imageUrl = makeFullUrl(series.backdropPath)
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )

    val toolbarHeightRange = with(LocalDensity.current) {
        Constant.MinToolbarHeight.roundToPx()..Constant.MaxToolbarHeight.roundToPx()
    }
    val toolbarState = rememberToolbarState(toolbarHeightRange)
    val scrollState = rememberScrollState()

    toolbarState.scrollValue = scrollState.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 5.dp)
            .nestedScroll(refreshState.nestedScrollConnection)
    ) {
        if (showImageScreen) {
            ImagesScreen(
                images = images,
                setShowImage = {
                    showImageScreen = it
                },
                isPoster = isPoster
            )
        }
        CustomAppBar(
            imagePainter = imagePainter,
            progress = toolbarState.progress,
            title = series.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                .graphicsLayer { translationY = toolbarState.offset },
            limit = toolbarState.progress != 0f,
            onIconClick = {
                navHostController.navigateUp()
            }
        )

        // Content
        TvSeriesDetailSection(
            modifier = Modifier.fillMaxSize(),
            series = series,
            scrollState = scrollState,
            detailState = seriesDetailState,
            changeFavoriteClick = {favorite, date, seriesId ->
                onEvent(SeriesDetailEvent.AddToFavorite(favorite = favorite, date = date, seriesId = seriesId))
            },
            showImage = {uri, title ->
                showImageScreen = true
                images.clear()
                images.addAll(uri)
                imageTitleToShow = "\"${series.name}\" $title"
                isPoster = title == "Poster"
            },
            navigateToCredit = {creditId ->
                navHostController.navigate("${NavRoute.CREDIT_SCREEN}?creditId=${creditId}")
            },
            onHomepageClick = {url ->
                navHostController.navigate("${NavRoute.WEB_VIEW}?url=$url")
            }
        )

        CustomAppBar(
            imagePainter = imagePainter,
            progress = toolbarState.progress,
            title = series.name,
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
fun TvSeriesDetailSection(
    modifier : Modifier = Modifier,
    series: TvSeries,
    scrollState: ScrollState,
    detailState: SeriesDetailState,
    changeFavoriteClick: (Int, String, Int) -> Unit,
    showImage: (List<String>, String) -> Unit,
    navigateToCredit: (Int) -> Unit,
    onHomepageClick: (String) -> Unit
){

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
                imageUrl = makeFullUrl(series.posterPath),
                modifier = Modifier.padding(start = 10.dp),
                width = 120.dp,
                height = 220.dp,
                onClick = {
                    showImage(listOf(series.posterPath) , "Poster")
                }
            )

            Spacer(modifier = Modifier.width(20.dp))

            TvSeriesInfoSection(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 50.dp),
                series = series,
                changeFavoriteClick = changeFavoriteClick
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

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
                    TvSeriesOverviewSection(
                        modifier = Modifier.fillMaxWidth(),
                        series = series,
                        navigate = {url ->
                            onHomepageClick(url)
                        }
                    )
                }

                1 -> {
                    RecommendationSection(
                        collections = emptyList(),
                        similar = detailState.similar,
                        navigate = {

                        }
                    )
                }

                2 -> {
                    CreditSection(id = series.id, credits = detailState.credits) {
                        navigateToCredit(it)
                    }
                }

                3 -> {
                    ImageSection(images = series.images) { uris, title ->
                        showImage(uris, title)
                    }
                }

                4 -> {
                    VideoSection(keys = series.videos) {
                        
                    }
                }
            }
        }
    }
}


@Composable
fun TvSeriesInfoSection(
    modifier: Modifier = Modifier,
    series: TvSeries,
    changeFavoriteClick: (Int, String, Int) -> Unit
) {
    val genres = getGenresFromCode(series.genres).joinToString(" - ") { it.name }

    val addedToFavorite = rememberSaveable {
        mutableStateOf(series.addedToFavorite)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        Text(text = series.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = if (series.adult) "+18" else "-13",
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
                if(series.firstAirDate.isNotEmpty()){
                    Text(
                        text =series.firstAirDate.convertDateFormat(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(
                        starsModifier = Modifier.size(12.dp),
                        rating = series.voteAverage / 2
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = series.voteAverage.toString().take(3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Button(
                onClick = {
                    val favorite = if(addedToFavorite.value) 0 else 1
                    val dateTime = LocalDateTime.now()
                    changeFavoriteClick(favorite, dateTime.toString(), series.id)
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