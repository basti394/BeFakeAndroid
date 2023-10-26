package pizza.xyz.befake.data

interface PostService {

}

@Singleton
class PostServiceImpl @Inject constructor(
    private val postAPI: PostAPI
): PostService {

    interface PostAPI {

    }
}