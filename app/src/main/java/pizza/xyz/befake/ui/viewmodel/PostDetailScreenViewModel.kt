package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.repository.FeedRepository
import pizza.xyz.befake.data.service.LoginService
import pizza.xyz.befake.data.service.PostService
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.utils.Utils.handle
import javax.inject.Inject

@HiltViewModel
class PostDetailScreenViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val postService: PostService,
    private val loginService: LoginService
) : ViewModel() {

    private var _post = MutableStateFlow<FriendsPosts?>(null)
    val post = _post.asStateFlow()

    fun getPost(username: String) {
        viewModelScope.launch {
            _post.value = feedRepository.getPostByUsername(username).first()
        }
    }

    fun commentPost(postId: String, comment: String) {
        if (postId.isBlank() || comment.isBlank()) return
        viewModelScope.launch {
            suspend { postService.comment(postId, comment) }.handle(
                onSuccess = {
                    getPost(post.value?.user?.username ?: "")
                },
                loginService = loginService
            )
        }
    }
}