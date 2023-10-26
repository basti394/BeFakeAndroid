package pizza.xyz.befake

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import pizza.xyz.befake.ui.screens.HomeScreen
import pizza.xyz.befake.ui.screens.LoginScreen
import pizza.xyz.befake.ui.viewmodel.LoginScreenViewModel
import pizza.xyz.befake.ui.viewmodel.LoginState

@HiltAndroidApp
class BeFakeApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {

    val viewModel: LoginScreenViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.height(100.dp),
                    title = {
                        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "BeFake.",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                    )
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent )
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Black
        ) {
            if (loginState != LoginState.LoggedIn) {
                LoginScreen(paddingValues = paddingValues)
            } else {
                HomeScreen(paddingValues = paddingValues)
            }
        }
    }
}

@Composable
@Preview
fun MainPreview() {
    Main()
}