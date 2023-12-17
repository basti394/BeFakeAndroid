package pizza.xyz.befake.ui.composables

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.compose.animation.core.animate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.Location
import pizza.xyz.befake.model.dtos.feed.Moment
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.RealMojis
import pizza.xyz.befake.utils.Utils.debugPlaceholderPost
import pizza.xyz.befake.utils.Utils.debugPlaceholderProfilePicture
import pizza.xyz.befake.utils.Utils.shimmerBrush
import pizza.xyz.befake.utils.Utils.testFeedPostLateThreeMinLocationBerlin
import pizza.xyz.befake.utils.Utils.testFeedPostNoLocation
import pizza.xyz.befake.utils.Utils.testFeedUser
import pizza.xyz.befake.utils.rememberCustomFlingBehaviour
import java.time.Instant
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

const val borderMargin = 50f
const val cornerRadius = 16

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Post(
    modifier: Modifier = Modifier,
    post: FriendsPosts?,
    myProfilePicture: String
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

        Column(
            modifier = modifier
        ) {
            Header(
                profilePicture = profilePicture,
                userName = userName,
                time = time,
                location = location,
                isLate = isLate,
                lateInSeconds = lateInSeconds
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
                    PostImages(post = post.posts[it])
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

            CaptionSection(post = post.posts[current], myProfilePicture = myProfilePicture)
        }
    }
}

@Composable
fun PostImages(
    post: Posts,
) {

    val coroutineScope = rememberCoroutineScope()
    var outerBoxSize by remember { mutableStateOf(Offset(0f, 0f)) }
    var visible by remember {
        mutableStateOf(true)
    }
    val haptic = LocalHapticFeedback.current
    val primary = remember { post.primary.url }
    val secondary = remember { post.secondary.url }
    var showPrimaryAsMain by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .width(LocalConfiguration.current.screenWidthDp.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .onSizeChanged {
                outerBoxSize = Offset(it.width.toFloat(), it.height.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        visible = false
                    },
                    onDragEnd = {
                        visible = true
                    },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        var offsetX by remember { mutableFloatStateOf(borderMargin) }
        var offsetY by remember { mutableFloatStateOf(borderMargin) }



        AsyncImage(
            model = secondary,
            contentDescription = "primary",
            placeholder = debugPlaceholderPost(id = R.drawable.post_example),
        )

        if (showPrimaryAsMain) {
            AsyncImage(
                model = primary,
                contentDescription = "primary",
                placeholder = debugPlaceholderPost(id = R.drawable.post_example),
            )
        }


        if (visible) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .height(160.dp)
                    .width(150.dp)
                    .align(Alignment.TopStart)
                    .pointerInput(Unit) {
                        val boxSize = this.size
                        detectDragGestures(onDragEnd = {
                            coroutineScope.launch {

                                val targetValWidth = if (offsetX > (outerBoxSize.x) / 3) {
                                    outerBoxSize.x - boxSize.width.toFloat() + borderMargin
                                } else {
                                    borderMargin
                                }

                                val jobX = async {
                                    animate(offsetX, targetValWidth) { it, _ ->
                                        offsetX = it
                                    }
                                }

                                val jobY = async {
                                    animate(offsetY, borderMargin) { it, _ ->
                                        offsetY = it
                                    }
                                }

                                jobX.await()
                                jobY.await()
                            }
                        }) { _, dragAmount ->
                            offsetX = (offsetX + dragAmount.x).coerceIn(
                                borderMargin,
                                outerBoxSize.x - (boxSize.width - borderMargin)
                            )
                            offsetY = (offsetY + dragAmount.y).coerceIn(
                                borderMargin,
                                outerBoxSize.y - (boxSize.height + borderMargin)
                            )
                        }
                    }
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(cornerRadius.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(cornerRadius.dp))
                        .clickable {
                            showPrimaryAsMain = !showPrimaryAsMain
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    placeholder = debugPlaceholderPost(id = R.drawable.post_example),
                    model = primary,
                    contentDescription = "primary"
                )

                if (showPrimaryAsMain) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(cornerRadius.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(cornerRadius.dp))
                            .clickable {
                                showPrimaryAsMain = !showPrimaryAsMain
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        placeholder = debugPlaceholderPost(id = R.drawable.post_example),
                        model = secondary,
                        contentDescription = "primary"
                    )
                }
            }
        }

        Reactions(
            modifier = Modifier.align(Alignment.BottomStart),
            reactions = post.realMojis
        )

        ActionButtons(
            modifier = Modifier.align(Alignment.BottomEnd)
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
        val reactionsPreview = reactions.takeLast(2)
        val rest = reactions.size - reactionsPreview.size
        if (rest > 0) {
            Box(
                modifier = modifier
                    .padding(start = 25.dp * (reactionsPreview.size))
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
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
                    contentDescription = "profilePicture"
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier
) {
    val iconSize = remember { 35.dp }
    Box(
        modifier = modifier
            .padding(10.dp),
    ) {
        Column {
            IconButton(
                onClick = { /*TODO*/ }
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
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Filled.TagFaces,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun PostLoading() {
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
            lateInSeconds = 0
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(550.dp)
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
    lateInSeconds: Int = 0
) {

    val context = LocalContext.current
    val localInspectionMode = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    var locationName: String? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = location) {
        location?.let {
            if (localInspectionMode) {
                locationName = "Berlin, Germany"
            } else {
                coroutineScope.launch {
                    locationName = withContext(Dispatchers.IO) {
                        getLocation(location, context)
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
            AsyncImage(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape),
                placeholder = debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                model = profilePicture,
                contentDescription = "profilePicture"
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
                            text = getTime(time),
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
                            text = getTimeLate(lateInSeconds, context),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
       }

        IconButton(
            onClick = { /*TODO*/ }
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "More",
                tint = Color.White
            )
        }
    }
}

fun getTime(time: String): String {
    val date = Instant.parse(time)
    val localDate = date.atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime()
    return if (localDate.hour < 10) "0${localDate.hour}" else localDate.hour.toString() + ":" + if (localDate.minute < 10) "0${localDate.minute}" else localDate.minute.toString()
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                placeholder = debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                model = myProfilePicture,
                contentDescription = "profilePicture"
            )
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

fun getLocation(
    location: Location,
    context: Context
): String? {
        
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    val cityName: String? = addresses?.get(0)?.locality
    val countryName: String? = addresses?.get(0)?.countryName

    if (cityName != null && countryName != null) {
        return "$cityName, $countryName"
    }
    return null
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

fun getTimeLate(
    lateInSeconds: Int,
    context: Context
) : String {
    return when {
        lateInSeconds < 60 -> {
            context.getString(R.string.sec_late, lateInSeconds.toString())
        }
        lateInSeconds < 3600 -> {
            context.getString(R.string.min_late, (lateInSeconds / 60).toString())
        }
        lateInSeconds < 86400 -> {
            context.getString(R.string.h_late, (lateInSeconds / 3600).toString())
        }
        else -> {
            "late"
        }
    }
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
        myProfilePicture = "https://ui-avatars.com/api/?name=&background=808080&size=100"
    )
}