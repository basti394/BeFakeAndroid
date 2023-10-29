package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.FriendsService
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.dtos.me.ProfilePicture
import javax.inject.Inject

@HiltViewModel
class BeFakeTopAppBarViewModel @Inject constructor(
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : ViewModel() {

    private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
    private val _username = MutableStateFlow("")

    val profilePictureUrl: StateFlow<String> = combine(_profilePicture, _username) { profilePicture, username ->
        if (username != "") profilePicture?.url ?: "https://ui-avatars.com/api/?name=${username.first()}&background=random&size=100"
        "https://ui-avatars.com/api/?name=&background=808080&size=100"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "https://ui-avatars.com/api/?name=&background=808080&size=100"
    )

    init {
        viewModelScope.launch {
            getProfilePicture()
        }
    }

    private suspend fun getProfilePicture() {
        friendsService.me().onSuccess {
            _profilePicture.value = it.data.profilePicture
            _username.value = it.data.username
        }.onFailure {
            println(it.message)
            loginService.refreshToken().onSuccess {
                getProfilePicture()
            }.onFailure { rt ->
                println(rt.message)
            }
        }
    }

}