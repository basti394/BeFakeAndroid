package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pizza.xyz.befake.Utils.handle
import pizza.xyz.befake.data.FriendsService
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.dtos.feed.FeedResponseDTO
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : ViewModel() {

    private val _feed: MutableStateFlow<FeedResponseDTO?> = MutableStateFlow(null)
    val feed = _feed.asStateFlow()

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState.Loading)
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getFeed()
        }
    }

    private suspend fun getFeed() {
        suspend { friendsService.feed() }.handle(
            onSuccess = {
                _feed.value = formatFeed(it)
                _state.value = HomeScreenState.Loaded
            },
            loginService = loginService
        )
    }

    private fun formatFeed(feedResponseDTO: FeedResponseDTO?) : FeedResponseDTO? {
        return feedResponseDTO
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    object Loaded : HomeScreenState()
    class Error(val message: String) : HomeScreenState()
}