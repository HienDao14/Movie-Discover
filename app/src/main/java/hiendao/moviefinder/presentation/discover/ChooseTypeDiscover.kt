package hiendao.moviefinder.presentation.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import hiendao.moviefinder.util.DataItemInDiscover
import hiendao.moviefinder.util.listCatalogInDiscover
import hiendao.moviefinder.util.listGenresInDiscover
import hiendao.moviefinder.util.listTypeInDiscover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseTypeDiscover(
    modifier: Modifier = Modifier,
    type: String,
    setShowDialog: (Boolean) -> Unit,
    setInfo: (DataItemInDiscover, Int) -> Unit,
    currentIndex: Int
) {
    val listItem = when (type) {
        "Type" -> listTypeInDiscover
        "Catalog" -> listCatalogInDiscover
        else -> listGenresInDiscover
    }

    Dialog(
        onDismissRequest = {
            setShowDialog(false)
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = type) },
                    navigationIcon = {
                        IconButton(onClick = { setShowDialog(false) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Navigate back"
                            )
                        }
                    }
                )
            }
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listItem.size) { index ->
                    val item = listItem[index]
                    Row(
                        modifier = if (index == currentIndex)
                            Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable {
                                    setInfo(item, index)
                                    setShowDialog(false)
                                }
                        else
                            Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clickable {
                                    setInfo(item, index)
                                    setShowDialog(false)
                                },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.name, fontSize = 18.sp, modifier = Modifier.padding(start = 10.dp))

                        if (index == currentIndex) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Chosen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}