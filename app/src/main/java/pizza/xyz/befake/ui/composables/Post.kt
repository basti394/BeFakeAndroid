package pizza.xyz.befake.ui.composables

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pizza.xyz.befake.R
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

        val userName = post.user.username
        val profilePictureUrl = post.user?.profilePicture?.url ?: ""
        val profilePicture = if (profilePictureUrl == "") profilePictureUrl else "https://ui-avatars.com/api/?name=${userName.first()}&background=random&size=100"
        val time = post.posts[0].takenAt

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
                        else -> pluralStringResource(R.plurals.view_comments, post.posts[0].comments.size)
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
    var mainImage by remember{ mutableStateOf(post.primary.url) }
    var littleImage by remember{ mutableStateOf(post.secondary.url) }

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
            model = mainImage,
            contentDescription = "primary",
            placeholder = debugPlaceholder(id = R.drawable.post_example),

            )
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
                            val tempMainImage = mainImage
                            mainImage = littleImage
                            littleImage = tempMainImage
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    placeholder = debugPlaceholder(id = R.drawable.post_example),
                    model = littleImage,
                    contentDescription = "primary"
                )
            }
        }

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
           modifier = Modifier.padding(horizontal = 16.dp),
           verticalAlignment = Alignment.CenterVertically
       ) {
            AsyncImage(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape),
                placeholder = debugPlaceholder(id = R.drawable.profile_picture_example),
                model = profilePicture,
                contentDescription = "profilePicture"
            )
            Column(
                 modifier = Modifier.padding(start = 8.dp)
            ) {

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val outputDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

                Text(
                      text = userName,
                      color = Color.White,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.SemiBold
                )
                Row(
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
                                modifier = Modifier
                                    .clickable {
                                        openInGoogleMaps(
                                            long = location.longitude,
                                            lat = location.latitude,
                                            userName = userName,
                                            context = context
                                        )
                                    },
                                text = it,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Dot()
                        }
                    }
                    Text(
                        text = inputFormat.parse(time)?.let { outputDateFormat.format(it) } ?: time,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    if (isLate) {
                        Dot()
                        Text(
                            text = getTimeLate(lateInSeconds),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
       }

        IconButton(onClick = { /*TODO*/ }) {
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
    lateInSeconds: Int
) : String {
    return when {
        lateInSeconds < 60 -> {
            "$lateInSeconds sec. late"
        }
        lateInSeconds < 3600 -> {
            "${lateInSeconds/60} min. late"
        }
        lateInSeconds < 86400 -> {
            "${lateInSeconds/3600} h. late"
        }
        else -> {
            "late"
        }
    }
}

@Composable
fun debugPlaceholder(@DrawableRes id: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = id)
    } else {
        null
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