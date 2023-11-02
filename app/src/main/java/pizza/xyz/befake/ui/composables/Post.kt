package pizza.xyz.befake.ui.composables

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.compose.animation.core.animate
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pizza.xyz.befake.R
import pizza.xyz.befake.Utils.debugPlaceholderPost
import pizza.xyz.befake.Utils.debugPlaceholderProfilePicture
import pizza.xyz.befake.Utils.shimmerBrush
import pizza.xyz.befake.Utils.testFeedPostLateThreeMinLocationBerlin
import pizza.xyz.befake.Utils.testFeedPostNoLocation
import pizza.xyz.befake.Utils.testFeedUser
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.Location
import pizza.xyz.befake.model.dtos.feed.Moment
import pizza.xyz.befake.model.dtos.feed.Posts
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

const val borderMargin = 50f
const val cornerRadius = 16

@Composable
fun Post(
    modifier: Modifier = Modifier,
    post: FriendsPosts?,
) {

    if (post == null) {
        Spacer(modifier = modifier.fillMaxSize())
    } else {

        val userName = remember { post.user.username }
        val profilePictureUrl = remember { post.user?.profilePicture?.url ?: "" }
        val profilePicture = remember {
            if (profilePictureUrl != "") profilePictureUrl else "https://ui-avatars.com/api/?name=${userName.first()}&background=random&size=100"
        }
        val time = remember {
            post.posts[0].takenAt
        }

        Column(
            modifier = modifier
        ) {
            Header(
                profilePicture = profilePicture,
                userName = userName,
                time = time,
                location = post.posts[0].location,
                isLate = post.posts[0].isLate,
                lateInSeconds = post.posts[0].lateInSeconds
            )

            if (post.posts.size == 1) {
                PostImages(post = post.posts[0])
            } else {
                LazyRow {
                    items(post.posts.size) {
                        Box {
                            PostImages(post = post.posts[it])
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                if (post.posts[0].caption?.isNotEmpty() == true) {
                    Text(
                        text = post.posts[0].caption ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = when (post.posts[0].comments.size) {
                        0 -> stringResource(R.string.add_comment)
                        1 -> stringResource(id = R.string.view_comment)
                        else -> stringResource(id = R.string.view_comments, post.posts[0].comments.size)
                    },
                    color = Color.Gray,
                )
            }
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
            .fillMaxWidth()
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
        var offsetX by remember { mutableStateOf(borderMargin) }
        var offsetY by remember { mutableStateOf(borderMargin) }

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
                    .size(160.dp)
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

    }
}

@Composable
fun PostLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Header(
            profilePicture = "",
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

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val outputDateFormat = SimpleDateFormat("HH:mm")

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
                        val string: String? = if (LocalInspectionMode.current) {
                            "Berlin, Germany"
                        } else {
                            getLocation(location, context)
                        }
                        string?.let {
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
                            text = inputFormat.parse(time)?.let { outputDateFormat.format(it) } ?: time,
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

@Composable
fun Dot() {
    Box(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .size(4.dp)
            .clip(CircleShape)
            .background(Color.Gray)
    )
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
        )
    )

    Post(
        post = friendsPost,
    )
}