package pizza.xyz.befake.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import pizza.xyz.befake.ui.viewmodel.PostDetailScreenViewModel
import pizza.xyz.befake.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    username: String,
    selectedPost: Int?,
    viewModel: PostDetailScreenViewModel = hiltViewModel(),
    onBack: () -> Unit ,
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

    LaunchedEffect(key1 = username) {
        viewModel.getPost(username)
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
                                text = post?.user?.username ?: "",
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = post?.posts?.get(0)?.takenAt?.slice(11..18) ?: "",
                                color = Color.Gray,
                                fontSize = 16.sp,
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
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Posts(post?.posts) { _ -> /*TODO*/ }
            SeparatorLine()
            Reactions(reactions?.reversed())
            SeparatorLine()
            Comments(
                comments = comments,
                userName = post?.user?.username
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
    realMojis: List<RealMojis>?
) {
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
                    modifier = Modifier.padding(horizontal = 10.dp),
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
                        text = realMoji?.user?.username ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
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

@Composable
fun Comments(
    comments: List<Comment>?,
    userName: String?
) {
    if (comments.isNullOrEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                Comment(comments[index])
            }
        }
    }
}

@Composable
fun Comment(
    comment: Comment
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            placeholder = Utils.debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
            model = comment.user.profilePicture.url,
            contentDescription = "profilePicture"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = comment.user.username,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start
            )
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
    PostDetailScreen(
        username = "test",
        selectedPost = 0,
        onBack = {  }
    )
}
