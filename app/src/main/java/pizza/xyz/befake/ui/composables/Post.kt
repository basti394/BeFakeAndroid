package pizza.xyz.befake.ui.composables

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.annotations.Async
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.ProfilePicture
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import kotlin.math.roundToInt

const val borderMargin = 50f
const val cornerRadius = 16

@Composable
fun Post(
    modifier: Modifier = Modifier,
    post: FriendsPosts? = null,
) {

    if (post == null) {
        Spacer(modifier = modifier.fillMaxSize())
    } else {

        val profilePicture = post.user?.profilePicture?.url ?: post.posts[0].secondary.url
        val userName = post.user.username
        val time = post.posts[0].takenAt

        Column(
            modifier = modifier
        ) {
            Header(
                profilePicture = profilePicture,
                userName = userName,
                time = time
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
                    text = "Add comment...",
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
            contentDescription = "primary"
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
                        //2147483647
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
    time: String
) {
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
                 model = profilePicture,
                 contentDescription = "profilePicture"
            )
            Column(
                 modifier = Modifier.padding(start = 8.dp)
            ) {

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val outputDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

                Text(
                      text = userName,
                      color = Color.White,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = inputFormat.parse(time)?.let { outputDateFormat.format(it) } ?: time,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
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
@Preview
fun PostPreview() {
    Post()
}