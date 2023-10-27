package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.FriendsService
import pizza.xyz.befake.model.dtos.feed.FeedResponseDTO
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val friendsService: FriendsService
) : ViewModel() {

    private val _feed: MutableStateFlow<FeedResponseDTO?> = MutableStateFlow(null)
    val feed = _feed.asStateFlow()

    init {
        viewModelScope.launch {
            val res = friendsService.feed()
            _feed.value = formatFeed(res.getOrNull())
        }
    }

    private fun formatFeed(feedResponseDTO: FeedResponseDTO?) : FeedResponseDTO? {
        return feedResponseDTO
    }
}