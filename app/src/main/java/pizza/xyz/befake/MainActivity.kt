package pizza.xyz.befake

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val otpCode by viewModel.optCode.collectAsStateWithLifecycle()
    
    val buttonText = when(loginState) {
        LoginState.PhoneNumber -> "Login"
        LoginState.OTPCode -> "Verify"
        LoginState.LoggedIn -> ""
    }

    val buttonEnabled = when(loginState) {
        LoginState.PhoneNumber -> phoneNumber.isNotEmpty()
        LoginState.OTPCode -> otpCode.length == 6
        LoginState.LoggedIn -> false
    }

    val onButtonClicked: () -> Unit = when(loginState) {
        LoginState.PhoneNumber -> viewModel::onLoginClicked
        LoginState.OTPCode -> viewModel::onVerifyClicked
        LoginState.LoggedIn -> fun() {}
    }

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
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (loginState != LoginState.LoggedIn) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 0.dp,
                                color = Color.Transparent,
                                shape = RoundedCornerShape(50f)
                            )
                            .clip(RoundedCornerShape(50f))
                            .background( if (buttonEnabled) Color.White else Color.Gray)
                            .clickable { if (buttonEnabled) onButtonClicked() }
                            .height(45.dp)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buttonText,
                            style = TextStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
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