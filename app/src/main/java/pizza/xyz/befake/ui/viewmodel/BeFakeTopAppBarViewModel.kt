package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.FriendsService
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.dtos.me.ProfilePicture
import pizza.xyz.befake.utils.Utils.handle
import javax.inject.Inject

@HiltViewModel
class BeFakeTopAppBarViewModel @Inject constructor(
    private val friendsService: FriendsService,
    private val loginService: LoginService
) : ViewModel() {

    private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
    private val _username = MutableStateFlow("")

    val profilePicture = _profilePicture.asStateFlow()
    val usernamePb = _username.map {
        if (_username.value.isEmpty()) {
            "https://ui-avatars.com/api/?name=&background=808080&size=100"
        } else {
            "https://ui-avatars.com/api/?name=${it.first()}&background=random&size=100"
        }
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
        suspend { friendsService.me() }.handle(
            onSuccess = {
                _profilePicture.value = it.data.profilePicture
                _username.value = it.data.username
            },
            loginService = loginService
        )
    }

}