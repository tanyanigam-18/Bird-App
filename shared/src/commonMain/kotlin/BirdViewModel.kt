import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.BirdImage

data class BirdUiState(
    val birdImage: List<BirdImage> = emptyList(),
    val selectedCategory : String? = null
){
    val categories = birdImage.map { it.category }.toSet()
    val selectedImages = birdImage.filter { it.category == selectedCategory}
}
class BirdViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(BirdUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateImage()
    }
    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation){
            json()
        }
    }

    suspend fun getImages() : List<BirdImage> {
        val images: List<BirdImage> = httpClient
            .get(urlString = "https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body()
        return  images
    }
     fun updateImage() {
         viewModelScope.launch {
             val images = getImages()
             _uiState.update { it.copy(birdImage = images) }

         }
     }

    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }

}