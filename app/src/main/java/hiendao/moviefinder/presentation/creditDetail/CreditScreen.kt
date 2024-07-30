package hiendao.moviefinder.presentation.creditDetail

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.R
import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.presentation.state.CreditState
import hiendao.moviefinder.presentation.uiEvent.CreditScreenEvent
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.convert.convertDateFormat
import hiendao.moviefinder.util.shared_components.CustomImage
import hiendao.moviefinder.util.shared_components.ImageScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScreen(
    modifier: Modifier = Modifier,
    creditState: CreditState,
    onEvent: (CreditScreenEvent) -> Unit,
    navHostController: NavHostController
) {

    var topAppBarTitle by remember {
        mutableStateOf("Credit Detail")
    }

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(CreditScreenEvent.Refresh("Credit"))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    var showImageScreen by remember {
        mutableStateOf(false)
    }
    var imageUriToShow by remember {
        mutableStateOf("")
    }
    var imageTitleToShow by remember {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = topAppBarTitle)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(refreshState.nestedScrollConnection)
                .padding(it)
        ) {
            if(showImageScreen && imageUriToShow.isNotEmpty()){
                ImageScreen(
                    uri = imageUriToShow,
                    title = imageTitleToShow,
                    setShowImage = {
                        showImageScreen = it
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (creditState.isLoading && creditState.loadingFor == "credit_detail") {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.Center)
                        .scale(0.5f)
                )
            }
            if (creditState.errorMsg != null) {
                Text(
                    text = "There is an error!!!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
            if (creditState.creditDetail != null) {
                topAppBarTitle = creditState.creditDetail.name
                CreditScreenLoaded(
                    modifier = Modifier.fillMaxSize(),
                    creditState = creditState,
                    onEvent = onEvent,
                    onMovieItemClick = {movieId ->
                        navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movieId}")
                    },
                    showImage = { profilePath, name ->
                        showImageScreen = true
                        imageUriToShow = profilePath
                        imageTitleToShow = name
                    }
                )
            }

            if(refreshState.isRefreshing){
                LaunchedEffect(true) {
                    refresh()
                }
            }

            LaunchedEffect(isRefreshing) {
                if(isRefreshing){
                    refreshState.startRefresh()
                } else {
                    refreshState.endRefresh()
                }
            }

            PullToRefreshContainer(state = refreshState, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun CreditScreenLoaded(
    modifier: Modifier = Modifier,
    creditState: CreditState,
    onEvent: (CreditScreenEvent) -> Unit,
    onMovieItemClick: (Int) -> Unit,
    showImage: (String, String) -> Unit
) {
    val context = LocalContext.current

    val credit = creditState.creditDetail!!
    val movies = creditState.movies.sortedByDescending { it.releaseDate }

    val facebookUrl = "https://m.facebook.com/${credit.externalIds[0]}"
    val instagramUrl = "http://instagram.com/_u/${credit.externalIds[1]}"
    val twitterUrl = "https://twitter.com/${credit.externalIds[2]}"

    val age =
        if (credit.birthday.isEmpty()) 0 else LocalDate.now().year - credit.birthday.substring(0, 4)
            .toInt()

    val smallFontSize = MaterialTheme.typography.bodySmall
    val mediumFontSize = MaterialTheme.typography.bodyMedium

    var ellipseState by remember {
        mutableStateOf(false)
    }

    var addedToFavorite by remember {
        mutableStateOf(credit.addedInFavorite)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {

            CustomImage(
                imageUrl = credit.profilePath,
                width = 120.dp,
                height = 180.dp,
                onClick = {
                    // Show image view
                    showImage(credit.profilePath, credit.name)
                }
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.padding(top = 5.dp)) {
                Row {
                    Text(
                        text = "Gender: ",
                        style = mediumFontSize,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = if (credit.gender == 1) "Female" else "Male",
                        style = mediumFontSize
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                credit.birthday.isNotEmpty().let {
                    Row {
                        Text(
                            text = "Age: ",
                            style = mediumFontSize,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(text = "$age years old", style = mediumFontSize)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Text(
                            text = "Birthday: ",
                            style = mediumFontSize,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(text = if(credit.birthday != "")credit.birthday.convertDateFormat() else "Unknown", style = mediumFontSize)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
                credit.placeOfBirth.isNotEmpty().let {
                    Text(
                        text = "Place Of Birth: ",
                        style = mediumFontSize,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(text = credit.placeOfBirth, style = mediumFontSize)
                }

            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(10.dp)),
            onClick = {
                onEvent(CreditScreenEvent.ChangeFavorite(favorite = if(addedToFavorite) 0 else 1, creditId = credit.id))
                if(creditState.changeFavorite == true){
                    addedToFavorite = !addedToFavorite
                } else {
                    Toast.makeText(context, "Some errors happended", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            if(!addedToFavorite){
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Add to favorite")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Add to favorites", style = mediumFontSize)
            } else {
                Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Added to favorite")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Added to favorites", style = mediumFontSize)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Text(text = "Biography", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            if (ellipseState) {
                Text(
                    text = credit.biography,
                    style = smallFontSize,
                    modifier = Modifier
                        .clickable {
                            ellipseState = false
                        }
                        .padding(start = 10.dp)
                )
            } else {
                Text(
                    text = credit.biography,
                    style = smallFontSize,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 10,
                    modifier = Modifier
                        .clickable {
                            ellipseState = true
                        }
                        .padding(start = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text(text = "Movie", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))
            if (movies.isEmpty()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(50.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .scale(0.5f)
                )
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(credit.movieId.size) { index ->
                        Column(
                            modifier = Modifier
                                .width(130.dp)
                                .clickable {
                                    onMovieItemClick(movies[index].id)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CustomImage(
                                imageUrl = movies[index].posterPath,
                                width = 120.dp,
                                height = 180.dp,
                                onClick = {
                                    //Navigate to movie detail
                                    onMovieItemClick(movies[index].id)
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "As ${credit.character[index]}",
                                style = smallFontSize,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Log.d("social media", credit.externalIds.joinToString(",") { it })

        Spacer(modifier = Modifier.height(10.dp))

        if(!(credit.externalIds[0] == "null" && credit.externalIds[1] == "null" && credit.externalIds[2] == "null")){
            Column {
                Text(text = "Social Media", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(10.dp))

                if (credit.externalIds[0] != "null" && credit.externalIds[0] != "") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clickable {
                                try {
                                    val uri = Uri.parse(facebookUrl)
                                    Intent(Intent.ACTION_VIEW, uri).also {
                                        it.`package` = "com.facebook.katana"
                                        context.startActivity(it)
                                    }
                                } catch (e: Exception) {
                                    val uri = Uri.parse(facebookUrl)
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.facebook_color),
                            contentDescription = "Visit ${credit.name}'s Facebook fanpage"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Facebook", style = mediumFontSize)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))


                if (credit.externalIds[1] != "null" && credit.externalIds[1] != "") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clickable {
                                val uri = Uri.parse(instagramUrl)
                                try {
                                    Intent(Intent.ACTION_VIEW, uri).also {
                                        it.`package` = "com.instagram.android"
                                        context.startActivity(it)
                                    }
                                } catch (e: Exception) {
                                    Intent(Intent.ACTION_VIEW, uri).also {
                                        context.startActivity(it)
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.instagram_color),
                            contentDescription = "Visit ${credit.name}'s instagram page",
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Instagram", style = mediumFontSize)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (credit.externalIds[2] != "null" && credit.externalIds[2] != "") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clickable {
                                val uri = Uri.parse(twitterUrl)
                                try {
                                    Intent(Intent.ACTION_VIEW, uri).also {
                                        it.`package` = "com.twitter.android"
                                        context.startActivity(it)
                                    }
                                } catch (e: Exception) {
                                    Intent(Intent.ACTION_VIEW, uri).also {
                                        context.startActivity(it)
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = if (isSystemInDarkTheme()) painterResource(id = R.drawable.twitter_dark) else
                                painterResource(id = R.drawable.twitter_light),
                            contentDescription = "Visit ${credit.name}'s X page",
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "X", style = mediumFontSize)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreditDetailPreview(
    modifier: Modifier = Modifier
) {
    val credit = Credit(
        adult = false,
        character = listOf("asd", "asd"),
        gender = 2, //1 - nu, 2 - nam
        id = 123012,
        knownForDepartment = "Acting",
        name = "Chris Hemsworth",
        popularity = 123.98,
        profilePath = "/5qHNjhtjMD4YWH3UP0rm4tKwxCL.jpg",
        department = "Acting",
        job = "",
        type = "Cast",
        movieId = listOf("123", "1233"),
        biography = "Robert John Downey Jr. (born April 4, 1965) is an American actor. His films as a leading actor have grossed over $14 billion worldwide, making him one of the highest-grossing actors of all time. Downey's career has been characterized by some early success, a period of drug-related problems and run-ins with the law, and a surge in popular and commercial success in the 2000s. In 2008, Downey was named by Time magazine as one of the 100 most influential people in the world. From 2013 to 2015, he was listed by Forbes as Hollywood's highest-paid actor.\n\nAt the age of five, Downey made his acting debut in his father Robert Downey Sr.'s film Pound in 1970. He subsequently worked with the Brat Pack in the teen films Weird Science (1985) and Less than Zero (1987). Downey's portrayal of Charlie Chaplin in the 1992 biopic Chaplin received a BAFTA Award. Following a stint at the Corcoran Substance Abuse Treatment Facility on drug charges, he joined the TV series Ally McBeal in 2000, and won a Golden Globe Award for the role. Downey was fired from the show in 2001 in the wake of additional drug charges. He stayed in a court-ordered drug treatment program and has maintained his sobriety since 2003.\n\nDowney made his acting comeback in the 2003 film The Singing Detective, after Mel Gibson paid his insurance bond because completion bond companies would not insure himю He went on to star in the black comedy Kiss Kiss Bang Bang (2005), the thriller Zodiac (2007), and the action comedy Tropic Thunder (2008). Downey gained global recognition for starring as Iron Man in ten films within the Marvel Cinematic Universe, beginning with Iron Man (2008), and leading up to Avengers: Endgame (2019). He has also played Sherlock Holmes in Guy Ritchie's Sherlock Holmes (2009), which earned him his second Golden Globe, and its sequel, Sherlock Holmes: A Game of Shadows (2011). Downey has also taken on dramatic parts in The Judge (2014) and Oppenheimer (2023), winning an Academy Award, a Golden Globe, and a BAFTA Award for his portrayal of Lewis Strauss in the latter.\n\nDescription above from the Wikipedia article Robert Downey Jr., licensed under CC-BY-SA, full list of contributors on Wikipedia.",
        birthday = "1965-04-04",
        deathday = null,
        homepage = null,
        placeOfBirth = "New York City, New York, USA",
        externalIds = listOf("robertdowneyjr", "RobertDowneyJr", "robertdowneyjr", "Q165219"),
        addedInFavorite = true
    )

    CreditScreenLoaded(
        modifier = modifier,
        creditState = CreditState(creditDetail = credit),
        onEvent = {},
        onMovieItemClick = {},
        showImage = {_, _ ->

        }
    )
}
