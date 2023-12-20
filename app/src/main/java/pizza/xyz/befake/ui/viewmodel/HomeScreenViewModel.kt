package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.repository.FeedRepository
import pizza.xyz.befake.data.service.FriendsService
import pizza.xyz.befake.data.service.LoginService
import pizza.xyz.befake.model.entities.Post
import pizza.xyz.befake.utils.Utils.handle
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : ViewModel() {

    private val _feed: MutableStateFlow<Post?> = MutableStateFlow(null)
    val feed = _feed.asStateFlow()

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState.Loading)
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private val _myProfilePicture: MutableStateFlow<String> = MutableStateFlow("")
    val myProfilePicture = _myProfilePicture.asStateFlow()

    init {
        viewModelScope.launch {
            feedRepository.getFeed().collect {
                _feed.value = it
                if (feed.value != null) _state.value = HomeScreenState.Loaded
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            feedRepository.updateFeed()
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