package pizza.xyz.befake

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import pizza.xyz.befake.model.dtos.feed.User
import pizza.xyz.befake.ui.composables.BeFakeTopAppBar
import pizza.xyz.befake.ui.screens.HomeScreen
import pizza.xyz.befake.ui.screens.LoginScreen
import pizza.xyz.befake.ui.screens.PostDetailScreen
import pizza.xyz.befake.ui.theme.BeFakeTheme
import pizza.xyz.befake.ui.viewmodel.LoginScreenViewModel
import pizza.xyz.befake.ui.viewmodel.LoginState

@HiltAndroidApp
class BeFakeApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: LoginScreenViewModel = hiltViewModel()
            val loginState by viewModel.loginState.collectAsStateWithLifecycle()
            val user by viewModel.user.collectAsStateWithLifecycle()
            BeFakeTheme {
                MainContent(
                    loginState,
                    user
                )
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    loginState: LoginState,
    user: User?
) {
    if (
        loginState != LoginState.LoggedIn
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBar(
                    loginState,
                    user
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                LoginScreen(paddingValues = paddingValues)
            }
        }
    } else {
        val navController: NavHostController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable(
                "home",
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(100)
                    )
                },
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                    )
                }
            ) {

                Scaffold(
                    topBar = {
                        BeFakeTopAppBar(
                            loginState,
                            user
                        )
                    },
                    containerColor = Color.Black
                ) { paddingValues ->
                    HomeScreen(
                        paddingValues = paddingValues,
                        openDetailScreen = { username, selectedPost, focusInput, focusRealMojis -> navController.navigate("post/$username?selectedPost=$selectedPost&focusInput=$focusInput&focusRealMojis=$focusRealMojis") }
                    )
                }
            }
            composable(
                "post/{username}?selectedPost={selectedPost}&focusInput={focusInput}&focusRealMojis={focusRealMojis}",
                arguments = listOf(
                    navArgument("username") {
                        defaultValue = ""
                    },
                    navArgument("selectedPost") {
                        defaultValue = 0
                    },
                    navArgument("focusInput") {
                        defaultValue = false
                    },
                    navArgument("focusRealMojis") {
                        defaultValue = false
                    }
                ),
                exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(100)
                    )
                },
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                    )
                }
            ) {
                val username = it.arguments?.getString("username")
                val selectedPost = it.arguments?.getInt("selectedPost")
                var focusInput by remember { mutableStateOf(it.arguments?.getBoolean("focusInput")) }
                val focusRealMojis = it.arguments?.getBoolean("focusRealMojis")
                if (username.isNullOrBlank()) throw IllegalStateException("Username cannot be null or blank")
                PostDetailScreen(
                    postUsername = username,
                    selectedPost = selectedPost,
                    focusInput = focusInput,
                    onBack = { navController.popBackStack() },
                    onCommentClick = { focusInput = true },
                    focusRealMojis = focusRealMojis,
                    myUser = user
                )
            }
        }

    }
}