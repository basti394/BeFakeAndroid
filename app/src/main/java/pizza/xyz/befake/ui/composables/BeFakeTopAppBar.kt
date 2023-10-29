package pizza.xyz.befake.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import pizza.xyz.befake.R
import pizza.xyz.befake.ui.viewmodel.BeFakeTopAppBarViewModel
import pizza.xyz.befake.ui.viewmodel.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeFakeTopAppBar(
    loginState: LoginState,
    viewModel: BeFakeTopAppBarViewModel = hiltViewModel()
) {

    val profilePicture by viewModel.profilePicture.collectAsStateWithLifecycle()

    BeFakeTopAppBarContent(loginState = loginState, profilePicture = profilePicture?.url ?: "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeFakeTopAppBarContent(
    loginState: LoginState,
    profilePicture: String
) {
    Column {
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
                Header(
                    loginState = loginState,
                    profilePicture = profilePicture
                )
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun Header(
    loginState: LoginState,
    profilePicture: String
) {
    Box(
        modifier = Modifier
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 30.dp),
            horizontalArrangement = if (loginState is LoginState.LoggedIn) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loginState is LoginState.LoggedIn) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Rounded.Group,
                        contentDescription = "More",
                        tint = Color.White
                    )
                }
            }
            Text(
                text = "BeFake.",
                color = Color.White,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            )
            if (loginState is LoginState.LoggedIn) {
                if (profilePicture == "") {
                    Box(modifier = Modifier
                        .clip(CircleShape)
                        .size(30.dp)
                        .background(Color.LightGray))
                } else {
                    AsyncImage(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        placeholder = debugPlaceholder(id = R.drawable.profile_picture_example),
                        model = profilePicture,
                        contentDescription = "profilePicture"
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun HeaderPreview() {
    BeFakeTopAppBarContent(
        loginState = LoginState.LoggedIn,
        profilePicture = "https://picsum.photos/1000/1000"
    )
}