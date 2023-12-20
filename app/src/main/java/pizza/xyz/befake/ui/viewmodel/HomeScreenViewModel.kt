package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.repository.FeedRepository
import pizza.xyz.befake.data.service.FriendsService
import pizza.xyz.befake.data.service.LoginService
import pizza.xyz.befake.utils.Utils.handle
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : ViewModel() {

    //private val _feed: MutableStateFlow<FeedResponseDTO?> = MutableStateFlow(null)
    val feed = feedRepository.feed.map { if (it != null) it.data else null }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null)

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState.Loading)
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private val _myProfilePicture: MutableStateFlow<String> = MutableStateFlow("")
    val myProfilePicture = _myProfilePicture.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            feedRepository.updateFeed().also {
                if (feed.value != null) _state.value = HomeScreenState.Loaded
            }
            getProfilePicture()
        }
    }

    private suspend fun getProfilePicture() {
        suspend { friendsService.me() }.handle(
            onSuccess = {
                _myProfilePicture.value = it.data.profilePicture?.url ?: ""
            },
            loginService = loginService
        )
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    object Loaded : HomeScreenState()
    class Error(val message: String) : HomeScreenState()
}