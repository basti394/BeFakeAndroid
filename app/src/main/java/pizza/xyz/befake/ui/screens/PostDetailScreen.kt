package pizza.xyz.befake.ui.screens

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.feed.Comment
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.RealMojis
import pizza.xyz.befake.model.dtos.feed.User
import pizza.xyz.befake.ui.composables.BeFakeInputField
import pizza.xyz.befake.ui.composables.Dot
import pizza.xyz.befake.ui.composables.DownloadPostMenu
import pizza.xyz.befake.ui.composables.PostImageState
import pizza.xyz.befake.ui.composables.PostImagesV2
import pizza.xyz.befake.ui.composables.ProfilePicture
import pizza.xyz.befake.ui.viewmodel.PostDetailScreenViewModel
import pizza.xyz.befake.utils.Utils
import pizza.xyz.befake.utils.Utils.formatRealMojis
import pizza.xyz.befake.utils.Utils.getLocation
import pizza.xyz.befake.utils.Utils.isScrolledToTheTop
import pizza.xyz.befake.utils.Utils.testFriendsPosts
import kotlin.math.absoluteValue

@Composable
fun PostDetailScreen(
    postUsername: String,
    myUser: User?,
    selectedPost: Int?,
    focusInput: Boolean?,
    viewModel: PostDetailScreenViewModel = hiltViewModel(),
    onBack: () -> Unit,
    focusRealMojis: Boolean?
) {

    val post by viewModel.post.collectAsStateWithLifecycle()

    var current by remember {
        mutableIntStateOf(selectedPost ?: 0)
    }

    val comments = remember(current, post) {
        post?.posts?.get(current)?.comments
    }
    val reactions = remember(current, post) {
        post?.posts?.get(current)?.realMojis?.let { formatRealMojis(it, myUser?.id) } ?: post?.posts?.get(current)?.realMojis?.reversed()
    }
    val takenAt = remember(current, post) {
        post?.posts?.get(current)?.takenAt
    }
    val posts = remember(current, post) {
        post?.posts
    }
    val myUserId = remember(myUser) {
        myUser?.id ?: ""
    }

    LaunchedEffect(key1 = postUsername) {
        viewModel.getPost(postUsername)
    }

    fun commentPost(comment: String) {
        viewModel.commentPost(post?.user?.id ?: "", post?.posts?.get(current)?.id ?: "", comment)
        viewModel.getPost(postUsername)
    }

    PostDetailScreenContent(
        comments = comments,
        reactions = reactions,
        username = postUsername,
        takenAt = takenAt,
        posts = posts,
        current = current,
        onBack = onBack,
        focusInput = focusInput,
        focusRealMojis = focusRealMojis,
        myUserId = myUserId,
        commentPost = ::commentPost
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun PostDetailScreenContent(
    comments: List<Comment>?,
    reactions: List<RealMojis>?,
    username: String,
    takenAt: String?,
    posts: List<Posts>?,
    current: Int,
    onBack: () -> Unit,
    focusInput: Boolean?,
    focusRealMojis: Boolean?,
    myUserId: String,
    commentPost: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var showBottomSheet by remember {
        mutableStateOf(focusRealMojis ?: false)
    }
    val sheetState = rememberModalBottomSheetState()

    var initialComment by remember {
        mutableStateOf("")
    }

    var focus by remember {
        mutableStateOf(focusInput ?: false)
    }

    var currentReactionDetail by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current

    var height by remember {
        mutableIntStateOf(200)
    }

    val lazyListState = rememberLazyListState()

    val velocityTracker = VelocityTracker()

    val pointerInput: suspend PointerInputScope.() -> Unit = {
        var direction = 0
        detectVerticalDragGestures(
            onDragEnd = {
                val velocity = velocityTracker.calculateVelocity()
                val velocityY = velocity.y
                if (velocityY.absoluteValue > 1000) {
                    if (direction < 0) {
                        if (height > 200) {
                            scope.launch {
                                animate(
                                    initialValue = height.toFloat(),
                                    targetValue = 200f,
                                    animationSpec = tween(300)
                                ) { value, _ ->
                                    height = value.toInt()
                                }
                            }
                        }
                    } else {
                        if (height < 450 && lazyListState.isScrolledToTheTop()) {
                            scope.launch {
                                animate(
                                    initialValue = height.toFloat(),
                                    targetValue = 450f,
                                    animationSpec = tween(300)
                                ) { value, _ ->
                                    height = value.toInt()
                                }
                            }
                        }
                    }
                }
                velocityTracker.resetTracking()
            }
        ) { change, dragAmount ->
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            if (!lazyListState.isScrollInProgress) {
                if (dragAmount < 0) {
                    direction = -1
                    if (height > 200) {
                        height -= 3
                    }
                } else {
                    direction = 1
                    if (height < 450 && lazyListState.isScrolledToTheTop()) {
                        height += 3
                    }
                }
            }
        }
    }

    var showDownloadMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black,
                                Color.Transparent
                            )
                        )
                    ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = username,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = takenAt?.let { Utils.getTime(it, true, context) } ?: "",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart)
                        ) {
                            IconButton(
                                onClick = { showDownloadMenu = !showDownloadMenu },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "More",
                                    tint = Color.White
                                )
                            }
                            DownloadPostMenu(
                                expanded = showDownloadMenu,
                                userName = username,
                                takenAt = posts?.get(current)?.takenAt ?: "",
                                btsLink = posts?.get(current)?.btsMedia?.url,
                                primaryLink = posts?.get(current)?.primary?.url ?: "",
                                secondaryLink = posts?.get(current)?.secondary?.url ?: "",
                                onDismissRequest = { showDownloadMenu = false },
                            )
                        }

                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                SeparatorLine(
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                BeFakeInputField(
                    modifier = Modifier
                        .padding(8.dp)
                        .navigationBarsPadding()
                        .imePadding()
                        .fillMaxWidth(),
                    onChange = {},
                    onSubmit = {
                        commentPost(it)
                        initialComment = ""
                        focus = false
                    },
                    clearValueOnSubmit = true,
                    placeholder = stringResource(R.string.schreibe_einen_kommentar),
                    focus = focus,
                    initialValue = initialComment,
                )
            }
        }
    ) {
        if (reactions?.isNotEmpty() == true) {
            ReactionDetailBottomSheet(
                showSheet = showBottomSheet,
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                realMoji = reactions[currentReactionDetail]
            )
        }
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit, pointerInput)
        ) {
            LazyColumn(
                state = lazyListState,
                //flingBehavior = customFlingBehaviour,
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                item(
                    key = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(1.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .pointerInput(Unit, pointerInput)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                scope.launch {
                                    animate(
                                        initialValue = height.toFloat(),
                                        targetValue = 450f,
                                        animationSpec = tween(350)
                                    ) { value, _ ->
                                        height = value.toInt()
                                    }
                                }
                            }

                    ) {
                        Posts(
                            posts,
                            current,
                            height
                        ) { _ -> /*TODO*/ }
                    }
                    SeparatorLine()
                    Reactions(
                        modifier = Modifier.pointerInput(Unit, pointerInput),
                        realMojis = reactions,
                        myUserId = myUserId,
                        onReactionClick = { index ->
                            currentReactionDetail = index
                            showBottomSheet = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                    SeparatorLine()
                }

                if (comments.isNullOrEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .pointerInput(Unit, pointerInput)
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Noch keine Kommentare",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Sei der Erste, der auf den Beitrag von $username reagiert.",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(
                        count = comments.size,
                        key = { index -> comments[index].id }
                    ) { index ->
                        Comment(
                            modifier = Modifier.pointerInput(Unit, pointerInput),
                            comment = comments[index],
                            onClick = {
                                initialComment = "@${comments[index].user.username} "
                                focus = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeparatorLine(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.DarkGray)
    )
}

@Composable
fun Posts(
    posts: List<Posts>?,
    current: Int,
    height: Int,
    onSwipe: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var locationName: String? by remember {
        mutableStateOf("")
    }
    posts?.let {
        LaunchedEffect(Unit) {
            scope.launch { it[current].location?.let {
                locationName = try {
                    getLocation(it, context)
                } catch (e: Exception) {
                    null
                }
            } }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PostImagesV2(
                post = it[current],
                showForeground = true,
                changeShowForeground = {},
                state = PostImageState.INTERACTABLE,
                height = height
            )
            Spacer(modifier = Modifier.height(16.dp))

            PostInformation(
                repostUsername = it[current].parentPostUsername,
                location = locationName,
                retakes = it[current].retakeCounter
            )
        }

    } ?: Box(
        modifier = Modifier
            .height(height.dp)
            .width((height * 0.75).dp)
            .clip(RoundedCornerShape((height * 0.03).dp))
            .background(Color.Gray, RoundedCornerShape((height * 0.03).dp)),
    )
}

@Composable
fun PostInformation(
    repostUsername: String?,
    location: String?,
    retakes: Int?,
) {
    val map = mapOf(
        repostUsername to Icons.Filled.Repeat,
        location to Icons.Filled.NearMe,
        retakes?.let { "$it Wiederholungen" } to Icons.Filled.Refresh
    )


    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            count = map.size,
            key = { index -> index }
        ) {
            val text = map.keys.toList()[it] ?: ""
            val icon = map.values.toList()[it]
            if (text.isBlank() || text.startsWith("0")) return@items
            InformationChip(text = text, icon = icon)
        }
    }
}

@Composable
fun InformationChip(
    text: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Utils.lightBlack)
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 2.dp, start = 12.dp, top = 5.dp, bottom = 5.dp)
                .size(16.dp),
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
        )
        Text(
            modifier = Modifier.padding(start = 2.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
            text = text,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
fun Reactions(
    modifier: Modifier = Modifier,
    realMojis: List<RealMojis>?,
    myUserId: String,
    onReactionClick: (Int) -> Unit
) {
    if (realMojis.isNullOrEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noch keine Reaktionen",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Sei der Erste, der auf den Beitrag reagiert.",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    } else {
        val containsMyRealMoji = realMojis.first().user.id == myUserId
        Row(
            modifier = modifier
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (containsMyRealMoji) {
                Reaction(
                    modifier = Modifier.padding(start = 16.dp),
                    myReaction = true,
                    realMoji = realMojis.first(),
                    onReactionClick = { onReactionClick(0) }
                )
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                )
            }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        return available.copy(x = 0f)
                    }
                }
            }
            LazyRow(
                modifier = Modifier
                    .nestedScroll(nestedScrollConnection)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Spacer(modifier = Modifier.width(16.dp))
                }
                items(realMojis.size) { index ->
                    if (index == 0 && containsMyRealMoji) return@items
                    Reaction(
                        modifier = if (index == 1) Modifier.padding(end = 10.dp) else Modifier.padding(horizontal = 10.dp),
                        realMoji = realMojis[index]
                    ) {
                        onReactionClick(index)
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

@Composable
fun Reaction(
    modifier: Modifier = Modifier,
    myReaction: Boolean = false,
    realMoji: RealMojis?,
    onReactionClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(90.dp)
            .clickable { onReactionClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (myReaction) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .size(90.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    placeholder = Utils.debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                    model = realMoji?.media?.url,
                    contentDescription = "profilePicture"
                )
            }
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = realMoji?.emoji ?: "",
                fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = realMoji?.user?.username ?: "",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionDetailBottomSheet(
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    realMoji: RealMojis?
) {
    if (showSheet) {
        ModalBottomSheet(
            windowInsets = WindowInsets(bottom = 0),
            containerColor = Color(0xFF131313),
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            ReactionDetail(realMoji = realMoji)
        }
    }
}

@Composable
fun ReactionDetail(
    realMoji: RealMojis?
) {

    val context = LocalContext.current

    Box(modifier = Modifier
        .height(350.dp)
        .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    placeholder = Utils.debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                    model = realMoji?.media?.url,
                    contentDescription = "profilePicture"
                )
                Text(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    text = realMoji?.emoji ?: "",
                    fontSize = 40.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = realMoji?.user?.username ?: "",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = realMoji?.postedAt?.let { Utils.getTime(it, true, context) } ?: "",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(16.dp))
            AssistChip(
                label = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                    )
                },
                onClick = { Utils.download(realMoji?.media?.url ?: "", "${realMoji?.user?.username}'s reaction", context) }
            )
        }
    }
}

@Composable
fun Comment(
    modifier: Modifier = Modifier,
    comment: Comment,
    onClick: (String) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick(comment.user.username) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape),
            profilePicture = comment.user.profilePicture?.url,
            username = comment.user.username
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.user.username,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Start
                )
                Dot()
                Text(
                    text = Utils.getTime(comment.postedAt, false, context),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start
                )
            }
            Text(
                text = comment.content,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.reply),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
@Preview
fun PostDetailScreenPreview() {
    PostDetailScreenContent(
        comments = testFriendsPosts.posts[0].comments,
        reactions = testFriendsPosts.posts[0].realMojis,
        username = testFriendsPosts.user.username,
        takenAt = testFriendsPosts.posts[0].takenAt,
        posts = testFriendsPosts.posts,
        onBack = {},
        focusInput = false,
        focusRealMojis = false,
        myUserId = testFriendsPosts.user.id,
        commentPost = {},
        current = 0,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ReactionDetailPreview() {
    ReactionDetail(realMoji = testFriendsPosts.posts[0].realMojis[0])
}
