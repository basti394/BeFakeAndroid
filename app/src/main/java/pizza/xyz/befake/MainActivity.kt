package pizza.xyz.befake

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
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
            BeFakeTheme {
                MainContent(loginState)
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    loginState: LoginState
) {
    if (
        loginState != LoginState.LoggedIn
    ) {
        Scaffold(
            topBar = {
                BeFakeTopAppBar(
                    loginState
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
        Scaffold(
            topBar = {
                BeFakeTopAppBar(
                    loginState
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(paddingValues = paddingValues)
                }
                composable("post/{username}") {
                    PostDetailScreen(post = )
                }
            }
        }
    }
}