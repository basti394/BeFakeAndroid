package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pizza.xyz.befake.data.FriendsService
import pizza.xyz.befake.model.dtos.me.ProfilePicture
import javax.inject.Inject

@HiltViewModel
class BeFakeTopAppBarViewModel @Inject constructor(
    private val friendsService: FriendsService
) : ViewModel() {

    private val _profilePicture = MutableStateFlow<ProfilePicture?>(null)
    val profilePicture = _profilePicture.asStateFlow()

    init {
        viewModelScope.launch {
            getProfilePicture()
        }
    }

    private suspend fun getProfilePicture() {
        withContext(Dispatchers.IO) {
            friendsService.me().getOrNull()?.let { meResponseDTO ->
                _profilePicture.value = meResponseDTO.data.profilePicture
            }
        }
    }

}