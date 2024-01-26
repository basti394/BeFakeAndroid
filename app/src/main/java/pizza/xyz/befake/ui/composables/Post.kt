package pizza.xyz.befake.ui.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.Location
import pizza.xyz.befake.model.dtos.feed.Moment
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.RealMojis
import pizza.xyz.befake.model.dtos.feed.User
import pizza.xyz.befake.utils.Utils
import pizza.xyz.befake.utils.Utils.debugPlaceholderProfilePicture
import pizza.xyz.befake.utils.Utils.formatRealMojis
import pizza.xyz.befake.utils.Utils.getLocation
import pizza.xyz.befake.utils.Utils.shimmerBrush
import pizza.xyz.befake.utils.Utils.testFeedPostLateThreeMinLocationBerlin
import pizza.xyz.befake.utils.Utils.testFeedPostNoLocation
import pizza.xyz.befake.utils.Utils.testFeedUser
import pizza.xyz.befake.utils.rememberCustomFlingBehaviour

const val borderMargin = 50f
const val cornerRadius = 16

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Post(
    modifier: Modifier = Modifier,
    post: FriendsPosts?,
    myUser: User?,
    openDetailScreen: (String, Int, Boolean, Boolean) -> Unit
) {

    val state = rememberLazyListState()
    LaunchedEffect(key1 = Unit) {
        state.scrollToItem(post?.posts?.size?.minus(1) ?: 0)
    }


    var current by remember {
        mutableIntStateOf(post?.posts?.size?.minus(1) ?: 0)
    }
    
    val flingBehavior = rememberCustomFlingBehaviour(
        lazyListState = state,
        onFling = { velocity ->
            current = if (velocity < 0) {
                (current - 1).coerceAtLeast(0)
            } else {
                (current + 1).coerceAtMost(post?.posts?.size?.minus(1) ?: 0)
            }
        }
    )

    if (post == null) {
        Spacer(modifier = modifier.fillMaxSize())
    } else {

        val userName = remember { post.user.username }
        val profilePictureUrl = remember { post.user?.profilePicture?.url ?: "" }
        val profilePicture = remember {
            if (profilePictureUrl != "") profilePictureUrl else "https://ui-avatars.com/api/?name=${userName.first()}&background=random&size=100"
        }
        val time = remember(current) {
            post.posts[current].takenAt
        }
        val location = remember(current) {
            post.posts[current].location
        }
        val isLate = remember(current) {
            post.posts[current].isLate
        }
        val lateInSeconds = remember(current) {
            post.posts[current].lateInSeconds
        }
        val btsLink = remember(current) {
            post.posts[current].btsMedia?.url
        }
        val primaryLink = remember(current) {
            post.posts[current].primary.url
        }
        val secondaryLink = remember(current) {
            post.posts[current].secondary.url
        }
        val parentUsername = remember(current) {
            post.posts[current].parentPostUsername
        }

        Column(
            modifier = modifier
        ) {
            Header(
                profilePicture = profilePicture,
                userName = userName,
                time = time,
                location = location,
                isLate = isLate,
                lateInSeconds = lateInSeconds,
                btsLink = btsLink,
                primaryLink = primaryLink,
                secondaryLink = secondaryLink,
                parentUsername = parentUsername,
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                state = state,
                flingBehavior = flingBehavior,
                userScrollEnabled = post.posts.size > 1
            ) {
                items(
                    post.posts.size,
                    key = { it }
                ) {
                    PostImages(
                        post = post.posts[it],
                        myUser = myUser,
                        openDetailScreen = { focusInput, focusRealMojis -> openDetailScreen(post.user.username, current, focusInput, focusRealMojis) }
                    )
                }
            }

            if (post.posts.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..post.posts.size).forEach {
                        Dot(
                            size = 6.dp,
                            color = if (it == current + 1) Color.White else Color.Gray
                        )
                    }
                }
            }

            CaptionSection(
                post = post.posts[current],
                myProfilePicture = myUser?.profilePicture?.url ?: "",
                username = myUser?.username ?: "",
                openDetailScreen = { focusInput -> openDetailScreen(post.user.username, current, focusInput, false) }
            )
        }
    }
}

@Composable
fun PostImages(
    post: Posts,
    myUser: User?,
    openDetailScreen: (Boolean, Boolean) -> Unit
) {

    var showForeground by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.Center)
    ) {
        PostImagesV2(
            post = post,
            showForeground = showForeground,
            changeShowForeground = { showForeground = it },
            state = PostImageState.INTERACTABLE,
            height = 550
        )

        if (showForeground) {
            Reactions(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .clickable { openDetailScreen(false, false) },
                reactions = formatRealMojis(post.realMojis, myUser?.id)
            )

            ActionButtons(
                modifier = Modifier.align(Alignment.BottomEnd),
                openDetailScreen = { focusInput -> openDetailScreen(focusInput, false) }
            )

            if (post.postType == "bts") {
                BtsBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                )
            }
        }
    }
}

@Composable
fun BtsBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .height(30.dp)
            .width(70.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(15.dp),
            imageVector = Icons.Outlined.SlowMotionVideo,
            contentDescription = "BTS",
            tint = Color.White,
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            text = "BTS",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Reactions(
    modifier: Modifier = Modifier,
    reactions: List<RealMojis>
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        val reactionsPreview = reactions.reversed().takeLast(2)
        val rest = reactions.size - reactionsPreview.size
        if (rest > 0) {
            Box(
                modifier = modifier
                    .padding(start = 25.dp * (reactionsPreview.size))
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF131313))
                    .size(35.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "+$rest",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        reactionsPreview.forEachIndexed { index, realMoji ->
            val padding = 25.dp * (reactionsPreview.size - (index + 1))
            Box(
                modifier = modifier
                    .padding(start = padding)
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .size(35.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape),
                    placeholder = debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                    model = realMoji.media.url,
                    contentDescription = "realMoji"
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    openDetailScreen: (Boolean) -> Unit
) {
    val iconSize = remember { 35.dp }
    Box(
        modifier = modifier
            .padding(10.dp),
    ) {
        Column {
            IconButton(
                onClick = { openDetailScreen(true) }
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "Comments",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(
                onClick = { openDetailScreen(false) }
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Filled.TagFaces,
                    contentDescription = "Reactions",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun PostLoading(
    height: Int = 550
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Header(
            profilePicture = "https://ui-avatars.com/api/?name=&background=808080&size=100",
            userName = "",
            time = "",
            location = null,
            isLate = false,
            lateInSeconds = 0,
            btsLink = null,
            primaryLink = "",
            secondaryLink = "",
            parentUsername = null,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(height.dp)
                .clip(RoundedCornerShape(cornerRadius.dp))
                .background(shimmerBrush())
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = stringResource(R.string.add_comment),
            color = Color.Gray,
        )
    }
}



@Composable
fun Header(
    profilePicture: String,
    userName: String,
    time: String,
    location: Location?,
    isLate: Boolean = false,
    lateInSeconds: Int = 0,
    btsLink: String?,
    primaryLink: String,
    secondaryLink: String,
    parentUsername: String?,
) {

    val context = LocalContext.current
    val localInspectionMode = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    var locationName: String? by remember { mutableStateOf(null) }

    var showDownloadMenu by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = location) {
        location?.let {
            if (localInspectionMode) {
                locationName = "Berlin, Germany"
            } else {
                coroutineScope.launch {
                    locationName = withContext(Dispatchers.IO) {
                        try {
                            getLocation(location, context)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicture(
                modifier = Modifier.size(35.dp),
                profilePicture = profilePicture,
                username = userName
            )
            Column(
                 modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.width((LocalConfiguration.current.screenWidthDp * 0.7f).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (parentUsername != null) {
                        Icon(
                            imageVector = Icons.Outlined.Repeat,
                            contentDescription = "Reply",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = parentUsername,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Dot()
                    }
                    if (location != null) {
                        locationName?.let {
                            Text(
                                maxLines = 1,
                                modifier = Modifier
                                    .clickable {
                                        openInGoogleMaps(
                                            long = location.longitude,
                                            lat = location.latitude,
                                            userName = userName,
                                            context = context
                                        )
                                    },
                                overflow = TextOverflow.Ellipsis,
                                text = it,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Dot()
                        }
                    }
                    if (time.isNotBlank()) {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = Utils.getTime(time, !isLate, context),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    if (isLate) {
                        Dot()
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = Utils.getTimeLate(lateInSeconds, context),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
       }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
        ) {
            IconButton(
                onClick = { showDownloadMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
            DownloadPostMenu(
                expanded = showDownloadMenu,
                userName = userName,
                takenAt = time,
                btsLink = btsLink,
                primaryLink = primaryLink,
                secondaryLink = secondaryLink,
                onDismissRequest = { showDownloadMenu = false },
            )
        }
    }
}

@Composable
fun Dot(
    modifier: Modifier = Modifier,
    size: Dp = 4.dp,
    color: Color = Color.Gray,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun CaptionSection(
    post: Posts,
    myProfilePicture: String,
    username: String,
    openDetailScreen: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        if (post.caption?.isNotEmpty() == true) {
            Text(
                text = post.caption,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.clickable { openDetailScreen(post.comments.size == 0) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicture(modifier = Modifier.size(30.dp), profilePicture = myProfilePicture, username = username)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = when (post.comments.size) {
                    0 -> stringResource(R.string.add_comment)
                    1 -> stringResource(id = R.string.view_comment)
                    else -> stringResource(id = R.string.view_comments, post.comments.size)
                },
                color = Color.Gray,
            )
        }

    }
}

fun openInGoogleMaps(
    long: Double,
    lat: Double,
    userName: String,
    context: Context
) {
    val uri = "geo:$lat,$long?q=$lat,$long($userName's BeReal)"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    context.startActivity(intent)
}

@Composable
@Preview
fun PostPreview() {
    val friendsPost = FriendsPosts(
        user = testFeedUser,
        momentId = "1",
        region = "de",
        moment = Moment(
            id = "1",
            region = "de"
        ),
        posts = listOf(
            testFeedPostLateThreeMinLocationBerlin,
            testFeedPostNoLocation
        ),
    )

    Post(
        post = friendsPost,
        myUser = User(
            id = "1",
            username = "test",
            profilePicture = null
        ),
        openDetailScreen = {_, _, _, _ ->}
    )
}