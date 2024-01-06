package pizza.xyz.befake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.feed.Comment
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.RealMojis
import pizza.xyz.befake.ui.composables.BeFakeInputField
import pizza.xyz.befake.ui.composables.Dot
import pizza.xyz.befake.ui.composables.ProfilePicture
import pizza.xyz.befake.ui.viewmodel.PostDetailScreenViewModel
import pizza.xyz.befake.utils.Utils
import pizza.xyz.befake.utils.Utils.testFriendsPosts

@Composable
fun PostDetailScreen(
    username: String,
    selectedPost: Int?,
    focusInput: Boolean?,
    viewModel: PostDetailScreenViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {

    val post by viewModel.post.collectAsStateWithLifecycle()

    var current by remember {
        mutableIntStateOf(selectedPost ?: 0)
    }

    val comments = remember(current, post) {
        post?.posts?.get(current)?.comments
    }
    val reactions = remember(current, post) {
        post?.posts?.get(current)?.realMojis
    }
    val takenAt = remember(current, post) {
        post?.posts?.get(current)?.takenAt
    }
    val posts = remember(current, post) {
        post?.posts
    }

    LaunchedEffect(key1 = username) {
        viewModel.getPost(username)
    }

    PostDetailScreenContent(
        comments = comments,
        reactions = reactions,
        username = username,
        takenAt = takenAt,
        posts = posts,
        onBack = onBack,
        focusInput = focusInput
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PostDetailScreenContent(
    comments: List<Comment>?,
    reactions: List<RealMojis>?,
    username: String,
    takenAt: String?,
    posts: List<Posts>?,
    onBack: () -> Unit,
    focusInput: Boolean?
) {
    val haptic = LocalHapticFeedback.current
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    var writeComment by remember {
        mutableStateOf(focusInput ?: false)
    }
    var initialComment by remember {
        mutableStateOf("")
    }

    var currentReactionDetail by remember {
        mutableIntStateOf(0)
    }

    val onCommentClick: (String) -> Unit = {
        writeComment = true
        initialComment = "@$it"
    }

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
                                text = takenAt?.let { Utils.getTime(it, true) } ?: "",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
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
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) {
        if (reactions?.isNotEmpty() == true) {
            ReactionDetailBottomSheet(
                showSheet = showBottomSheet,
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                realMoji = reactions.reversed()[currentReactionDetail]
            )
        }

        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .imeNestedScroll()
                    .padding(it)
            ) {
                item {
                    Posts(posts) { _ -> /*TODO*/ }
                    SeparatorLine()
                    Reactions(
                        reactions?.reversed(),
                        onReactionClick = { index ->
                            currentReactionDetail = index
                            showBottomSheet = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                    SeparatorLine()
                    Comments(
                        comments = comments,
                        userName = username,
                        onClick = onCommentClick
                    )
                }
            }

            BeFakeInputField(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxWidth(),
                onChange = {},
                onSubmit = {},
                placeholder = stringResource(R.string.schreibe_einen_kommentar),
                focus = writeComment,
                initialValue = initialComment,
            )
        }
    }
}

@Composable
fun SeparatorLine() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.DarkGray)
    )
}

@Composable
fun Posts(
    posts: List<Posts>?,
    onSwipe: (Int) -> Unit
) {
    Box(modifier = Modifier.height(200.dp))
}

@Composable
fun Reactions(
    realMojis: List<RealMojis>?,
    onReactionClick: (Int) -> Unit
) {
    if (realMojis.isNullOrEmpty()) {
        Column(
            modifier = Modifier
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
        Box(
            modifier = Modifier
                .height(150.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                items(realMojis?.size ?: 0) { index ->
                    val realMoji = realMojis?.get(index)
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .width(90.dp)
                            .clickable { onReactionClick(index) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                placeholder = Utils.debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                                model = realMoji?.media?.url,
                                contentDescription = "profilePicture"
                            )
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
                item {
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
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
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
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
fun Comments(
    comments: List<Comment>?,
    userName: String?,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
    ) {
        if (comments.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
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
                    text = "Sei der Erste, der auf den Beitrag von $userName reagiert.",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                items(
                    count = comments.size,
                    key = { index -> comments[index].id }
                ) { index ->
                    Comment(
                        comment = comments[index],
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
fun Comment(
    comment: Comment,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { onClick(comment.user.username) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(30.dp)
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
                    text = Utils.getTime(comment.postedAt, false),
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
        focusInput = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ReactionDetailPreview() {
    ReactionDetail(realMoji = testFriendsPosts.posts[0].realMojis[0])
}
