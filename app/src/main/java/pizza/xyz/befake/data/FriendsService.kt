package pizza.xyz.befake.data

import javax.inject.Inject
import javax.inject.Singleton

interface FriendsService {


}

@Singleton
class FriendsServiceImpl @Inject constructor(
    private val friendsAPI: FriendsAPI
): FriendsService {

    interface FriendsAPI {


    }
}
