package pizza.xyz.befake.ui.composables

import android.net.Uri
import androidx.compose.animation.core.animate
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.utils.Utils
import kotlin.math.roundToInt

enum class PostImageState {
    INTERACTABLE,
    STATIC
}

@Composable
fun PostImagesV2(
    post: Posts,
    state: PostImageState,
    height: Int,
    width: Int,
) {

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    var outerBoxSize by remember { mutableStateOf(Offset(0f, 0f)) }
    var showForeground by remember {
        mutableStateOf(true)
    }
    val haptic = LocalHapticFeedback.current
    val primary = remember { post.primary.url }
    val secondary = remember { post.secondary.url }
    var showPrimaryAsMain by remember {
        mutableStateOf(true)
    }

    val isBTS = remember {
        post.postType == "bts" && post.btsMedia != null
    }

    val exoPlayer = remember {
        if (isBTS) {
            SimpleExoPlayer.Builder(context).build().apply {
                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.packageName))

                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(
                        Uri.parse(
                            // Big Buck Bunny from Blender Project
                            post.btsMedia?.url
                        ))
                this.setMediaSource(source)
                this.prepare()
            }
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .width((LocalConfiguration.current.screenWidthDp + 1).dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .onSizeChanged {
                outerBoxSize = Offset(it.width.toFloat(), it.height.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        if (state != PostImageState.INTERACTABLE) return@detectDragGesturesAfterLongPress
                        showForeground = false
                    },
                    onDragEnd = {
                        if (state != PostImageState.INTERACTABLE) return@detectDragGesturesAfterLongPress
                        showForeground = true
                        exoPlayer?.pause()
                        exoPlayer?.seekTo(0L)
                    },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        var offsetX by remember { mutableFloatStateOf(borderMargin) }
        var offsetY by remember { mutableFloatStateOf(borderMargin) }


        if (!showForeground && (post.postType == "bts" && post.btsMedia != null) && state == PostImageState.INTERACTABLE) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            exoPlayer?.play()
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        exoPlayer.pause()
                        showForeground = true
                        exoPlayer.seekTo(0L)
                    }
                }
            })

            Box(
                modifier = Modifier
                    .height(550.dp)
                    .width(LocalConfiguration.current.screenWidthDp.dp)
                    .clip(RoundedCornerShape(cornerRadius.dp)),
            ) {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            player = exoPlayer
                            this.controllerAutoShow = false
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                        }
                    }
                )
            }
        } else {
            AsyncImage(
                model = secondary,
                contentDescription = "primary",
                placeholder = Utils.debugPlaceholderPost(id = R.drawable.post_example),
            )

            if (showPrimaryAsMain) {
                AsyncImage(
                    model = primary,
                    contentDescription = "primary",
                    placeholder = Utils.debugPlaceholderPost(id = R.drawable.post_example),
                )
            }
        }

        if (showForeground) {
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
                            if (state != PostImageState.INTERACTABLE) return@clickable
                            showPrimaryAsMain = !showPrimaryAsMain
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    placeholder = Utils.debugPlaceholderPost(id = R.drawable.post_example),
                    model = primary,
                    contentDescription = "primary"
                )

                if (showPrimaryAsMain) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(cornerRadius.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(cornerRadius.dp))
                            .clickable {
                                if (state != PostImageState.INTERACTABLE) return@clickable
                                showPrimaryAsMain = !showPrimaryAsMain
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        placeholder = Utils.debugPlaceholderPost(id = R.drawable.post_example),
                        model = secondary,
                        contentDescription = "primary"
                    )
                }
            }
        }
    }
}