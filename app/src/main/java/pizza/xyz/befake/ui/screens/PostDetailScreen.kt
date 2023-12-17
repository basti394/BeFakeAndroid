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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.feed.Comment
import pizza.xyz.befake.model.dtos.feed.FriendsPosts
import pizza.xyz.befake.model.dtos.feed.Posts
import pizza.xyz.befake.model.dtos.feed.RealMojis
import pizza.xyz.befake.utils.Utils
import pizza.xyz.befake.utils.Utils.testFriendsPosts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: FriendsPosts,
) {

    var current by remember {
        mutableIntStateOf(post.posts.size.minus(1))
    }
    val comments = remember(current) {
        post.posts[current].comments
    }
    val reactions = remember(current) {
        post.posts[current].realMojis
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
                        IconButton(onClick = { /*TODO*/ }) {
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
                                text = post.user.username,
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = post.posts[0].takenAt,
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
            Posts(post.posts) { _ -> /*TODO*/ }
            SeparatorLine()
            Reactions(reactions)
            SeparatorLine()
            Comments(
                comments = comments,
                userName = post.user.username
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
    posts: List<Posts>,
    onSwipe: (Int) -> Unit
) {
    Box(modifier = Modifier.height(200.dp))
}

@Composable
fun Reactions(
    realMojis: List<RealMojis>
) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(start = 10.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(realMojis.size) { index ->
                val realMoji = realMojis[index]
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box {
                        AsyncImage(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            placeholder = Utils.debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
                            model = realMoji.user.profilePicture.url,
                            contentDescription = "profilePicture"
                        )
                        Text(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            text = realMoji.emoji,
                            fontSize = 25.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = realMoji.user.username,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun Comments(
    comments: List<Comment>,
    userName: String
) {
    if (comments.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noch keine Kommentare",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = "Sei der Erste, der auf den Beitrag von $userName reagiert!",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    } else {
        Text(text = comments.first().content)
    }
}

@Composable
@Preview
fun PostDetailScreenPreview() {
    PostDetailScreen(
        post = testFriendsPosts
    )
}
