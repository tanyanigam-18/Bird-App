import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Chip
import androidx.compose.material.ChipColors
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.BirdImage
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val viewModel: BirdViewModel =
            getViewModel(key = Unit, factory = viewModelFactory { BirdViewModel() })
        BirdsImage(viewModel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BirdsImage(birdViewModel: BirdViewModel) {
    val uiState by birdViewModel.uiState.collectAsState()
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally
    ) {
        println(uiState.birdImage)
        LazyRow {
            items(uiState.categories.size) {index ->
            val category = uiState.categories.elementAt(index)
                val color = if(uiState.selectedCategory == category) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                Chip(onClick = {birdViewModel.selectCategory(category)}, colors = ChipDefaults.chipColors(backgroundColor = color)){
                    Text(category)
                }
            }
        }
        AnimatedVisibility(uiState.birdImage.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
            ) {
                   val list = if(uiState.selectedCategory == null) uiState.birdImage else uiState.selectedImages
                items(list) {
                    Text(it.toString())
                    BirdImageCell(it)
                }
            }
        }
    }
}

@Composable
fun BirdImageCell(image: BirdImage) {

    KamelImage(
        asyncPainterResource("https://sebastianaigner.github.io/demo-image-api/${image.path}"),
        "${image.category} by ${image.author}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f)
    )
}

expect fun getPlatformName(): String