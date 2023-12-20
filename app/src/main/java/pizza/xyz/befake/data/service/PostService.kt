package pizza.xyz.befake.data.service

import javax.inject.Inject
import javax.inject.Singleton

interface PostService {

}

@Singleton
class PostServiceImpl @Inject constructor(
    private val postAPI: PostAPI
): PostService {

    interface PostAPI {

    }
}