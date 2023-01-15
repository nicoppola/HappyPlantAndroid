package com.example.happyplantandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.happyplantandroid.ui.theme.HappyPlantAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel = MainViewModel(PlantRepository(InfluxDbService()))
        setContent {
            HappyPlantAndroidTheme {
                PlantApp(mainViewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantApp(viewModel: MainViewModel) {
    Scaffold(
        topBar = { TopBar() },
        content = { padding ->
            PaddingValues(horizontal = 4.dp)
            when (val state = viewModel.uiState.collectAsState().value) {
                is PlantUiState.Empty -> PlantDataContent(
                    plantList = emptyList(),
                    onRefresh = state.onRefresh,
                    isRefreshing = viewModel.isRefreshing
                )
                is PlantUiState.Error -> ErrorDialog(message = state.message)
                is PlantUiState.Loaded -> {
                    PlantDataContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        state.data,
                        state.onRefresh,
                        viewModel.isRefreshing
                    )
                }
                PlantUiState.Loading -> LoadingContent()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        modifier = Modifier,
        title = { Text("Happy Plant") },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Menu, "menuIcon")
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Settings, "settingsIcon")
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlantDataContent(
    modifier: Modifier = Modifier,
    plantList: List<PlantUiModel>,
    onRefresh: () -> Unit,
    isRefreshing: StateFlow<Boolean>
) {

    val isLoading by isRefreshing.collectAsState()

    val pullRefreshState =
        rememberPullRefreshState(refreshing = isLoading,
            onRefresh = { onRefresh() })


    Box(modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            state = rememberLazyListState()
        )
        {
            items(items = plantList) { condition ->
                ConditionCard(condition = condition)
            }
        }
        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun ConditionCard(condition: PlantUiModel) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            //Label
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = condition.location)
            }
            //Temp & Humidity
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
            ) {
                Row {
                    Text(text = condition.temperature)
                }
                Row {
                    Text(text = condition.humidity)
                }
            }
        }
    }
}


@Composable
fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorDialog(message: String) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false },
            title = { Text(text = stringResource(R.string.error_dialog_title)) },
            text = { Text(message) },
            confirmButton = { openDialog.value = false })
    }
}

@Preview
@Composable
fun PlantContentPreview() {
    HappyPlantAndroidTheme {
        PlantDataContent(
            plantList = listOf(
                PlantUiModel("Bedroom", "45%", "75°"),
                PlantUiModel("Office", "30%", "66°"),
                PlantUiModel("Kitchen", "36%", "68°")
            ),
            onRefresh = {},
            isRefreshing = MutableStateFlow<Boolean>(false).asStateFlow()
        )
    }
}

@Preview
@Composable
fun ErrorDialogPreview() {
    HappyPlantAndroidTheme {
        ErrorDialog(message = "This is a test error")
    }
}

@Preview
@Composable
fun TopBarPreview() {
    HappyPlantAndroidTheme {
        TopBar()
    }
}