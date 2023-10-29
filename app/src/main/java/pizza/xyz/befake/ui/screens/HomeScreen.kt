
package pizza.xyz.befake.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pizza.xyz.befake.Utils.testFeedPostLateThreeMinLocationBerlin
import pizza.xyz.befake.Utils.testFeedPostNoLocation
import pizza.xyz.befake.Utils.testFeedUser
import pizza.xyz.befake.model.dtos.feed.Data
import pizza.xyz.befake.model.dtos.feed.FeedResponseDTO
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.Location
import pizza.xyz.befake.model.dtos.feed.Moment
import pizza.xyz.befake.model.dtos.feed.PostData
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.Primary
import pizza.xyz.befake.model.dtos.feed.ProfilePicture
import pizza.xyz.befake.model.dtos.feed.Secondary
import pizza.xyz.befake.model.dtos.feed.User
import pizza.xyz.befake.model.dtos.feed.UserPosts
import pizza.xyz.befake.ui.composables.BeFakeTopAppBarContent
import pizza.xyz.befake.ui.composables.Post
import pizza.xyz.befake.ui.theme.BeFakeTheme
import pizza.xyz.befake.ui.viewmodel.HomeScreenViewModel
import pizza.xyz.befake.ui.viewmodel.LoginState

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {

    val feed by homeScreenViewModel.feed.collectAsStateWithLifecycle()

    HomeScreenContent(
        feed = feed,
    )
}

@Composable
fun HomeScreenContent(
    feed: FeedResponseDTO?,
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val count = feed?.data?.data?.friendsPosts?.size ?: 3

        items(count) {
            if (it == 0) Spacer(modifier = Modifier.height(90.dp))
            Post(
                post = feed?.data?.data?.friendsPosts?.reversed()?.get(it),
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreenPreview() {

    val feed = FeedResponseDTO(
        status = 200,
        message = "Success",
        data = Data(
            done = true,
            msg = "msg",
            data = PostData(
                userPosts = UserPosts(
                    user = testFeedUser,
                    region = "de",
                    momentId = "1",
                    posts = listOf(
                        testFeedPostNoLocation
                    )
                ),
                friendsPosts = listOf(
                    FriendsPosts(
                        user = testFeedUser,
                        momentId = "1",
                        region = "de",
                        moment = Moment(
                            id = "1",
                            region = "de"
                        ),
                        posts = listOf(
                            Posts(
                                id = "1",
                                takenAt = "2021-09-18T12:00:00.000Z",
                                caption = "caption",
                                primary = Primary(
                                    url = "https://picsum.photos/1500/2000",
                                    width = 1500,
                                    height = 2000,
                                    mediaType = "image"
                                ),
                                secondary = Secondary(
                                    url = "https://picsum.photos/1500/2000",
                                    width = 1500,
                                    height = 2000,
                                    mediaType = "image"
                                ),
                                comments = listOf(),
                                location = Location(
                    latitude = 52.5207,
                    longitude = 13.3733
                ),
                                retakeCounter = 0,
                                isLate = true,
                                isMain = true,
                                realMojis = emptyList(),
                                tags = emptyList(),
                                creationDate = "2021-09-18T12:00:00.000Z",
                                updatedAt = "2021-09-18T12:00:00.000Z",
                                visibility = emptyList(),
                                lateInSeconds = 187
                            )
                        )
                    ),
                    FriendsPosts(
                        user = User(
                            id = "1",
                            username = "username",
                            profilePicture = ProfilePicture(
                                url = "https://picsum.photos/1000/1000",
                                width = 500,
                                height = 500
                            )
                        ),
                        momentId = "1",
                        region = "de",
                        moment = Moment(
                            id = "1",
                            region = "de"
                        ),
                        posts = listOf(
                            testFeedPostLateThreeMinLocationBerlin
                        )
                    ),
                    FriendsPosts(
                        user = User(
                            id = "1",
                            username = "username",
                            profilePicture = ProfilePicture(
                                url = "",
                                width = 500,
                                height = 500
                            )
                        ),
                        momentId = "1",
                        region = "de",
                        moment = Moment(
                            id = "1",
                            region = "de"
                        ),
                        posts = listOf(
                            testFeedPostNoLocation
                        )
                    ),
                ),
                remainingPosts = 0,
                maxPostsPerMoment = 3
            )
        )
    )

    BeFakeTheme(
        darkTheme = true
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBarContent(
                    loginState = LoginState.LoggedIn,
                    profilePicture = "https://picsum.photos/1000/1000"
                )
            }
        ) { padding ->
            val o = padding
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Black
            ) {
                HomeScreenContent(
                    feed = feed
                )
            }
        }
    }

}