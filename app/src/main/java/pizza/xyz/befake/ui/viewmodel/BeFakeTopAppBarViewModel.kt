package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.service.FriendsService
import pizza.xyz.befake.data.service.LoginService
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
    val username = _username.asStateFlow()

    init {
        viewModelScope.launch {
            loginService.loginState.collect {
                if (it is LoginState.LoggedIn) {
                    getProfilePicture()
                }
            }
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