package hiendao.moviefinder.presentation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.model.BottomBarItem
import hiendao.moviefinder.presentation.state.MainUIState


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainUIState: MainUIState,
    navHostController: NavHostController
) {
    var screenSelected by remember {
        mutableIntStateOf(0)
    }

    val bottomItems = listOf(
        BottomBarItem(
            title = "Home",
            iconSelected = Icons.Filled.Home,
            iconUnSelected = Icons.Outlined.Home
        ),
        BottomBarItem(
            title = "Popular",
            iconSelected = Icons.Filled.LocalFireDepartment,
            iconUnSelected = Icons.Outlined.LocalFireDepartment
        ),
        BottomBarItem(
            title = "Tv Series",
            iconSelected = Icons.Filled.LiveTv,
            iconUnSelected = Icons.Outlined.LiveTv
        ),
        BottomBarItem(
            title = "Favorite",
            iconSelected = Icons.Filled.Favorite,
            iconUnSelected = Icons.Outlined.FavoriteBorder
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                bottomItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == screenSelected,
                        onClick = { screenSelected = index },
                        icon = {
                            if (index == screenSelected) {
                                Icon(imageVector = item.iconSelected, contentDescription = item.title)
                            } else
                                Icon(imageVector = item.iconUnSelected, contentDescription = item.title)
                        },
                        label = {
                            Text(text = item.title)
                        }
                    )
                }
            }
        }
    ) { paddingValue ->
        Box(modifier = modifier
            .fillMaxSize()
            .padding(bottom = paddingValue.calculateBottomPadding())){
            when(screenSelected){
                0 -> HomeScreen(modifier = Modifier.fillMaxSize(), uiState = mainUIState, navHostController = navHostController)
                1 -> HomeScreen(modifier = Modifier.fillMaxSize(), uiState = mainUIState, navHostController = navHostController)
                2 -> HomeScreen(modifier = Modifier.fillMaxSize(), uiState = mainUIState, navHostController = navHostController)
                3 -> HomeScreen(modifier = Modifier.fillMaxSize(), uiState = mainUIState, navHostController = navHostController)
            }
        }
    }
}