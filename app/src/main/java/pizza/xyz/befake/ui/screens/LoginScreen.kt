package pizza.xyz.befake.ui.screens

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pizza.xyz.befake.Main
import pizza.xyz.befake.ui.viewmodel.LoginScreenViewModel
import pizza.xyz.befake.ui.viewmodel.LoginState

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val otpCode by viewModel.optCode.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

    val headerText = when(loginState) {
        LoginState.PhoneNumber -> "Let's get started. Whats your phone number?"
        LoginState.OTPCode -> "Enter the code we just sent to your phone"
        LoginState.LoggedIn -> "Welcome back!"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = headerText,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        when(loginState) {
            LoginState.PhoneNumber -> PhoneNumberInput(
                value = phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                focusRequester = focusRequester
            )
            LoginState.OTPCode -> OTPCodeInput(
                value = otpCode,
                onValueChange = viewModel::onOptCodeChanged,
                focusRequester = focusRequester
            )
            LoginState.LoggedIn -> Spacer(modifier = Modifier)
        }

    }
}


@Composable
fun PhoneNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {

    val textStyle: (color: Color) -> TextStyle = { color ->
        TextStyle(
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
        )
    }

    val textSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.White,
    )

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        BasicTextField(
            singleLine = true,
            modifier = Modifier
                .focusRequester(focusRequester),
            textStyle = textStyle(Color.White),
            value = value,
            onValueChange = {
                if (it.length <= 16) onValueChange(it)
            },
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = "Phone Number",
                    style = textStyle(Color.Gray)
                )
            }
            innerTextField()
        }
    }
}

@Composable
fun OTPCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {

        val textStyle: (color: Color) -> TextStyle = { color ->
            TextStyle(
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
            )
        }

        val textSelectionColors = TextSelectionColors(
            handleColor = Color.White,
            backgroundColor = Color.White,
        )

        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }

        CompositionLocalProvider(
            LocalTextSelectionColors provides textSelectionColors
        ) {
            BasicTextField(
                singleLine = true,
                modifier = Modifier
                    .focusRequester(focusRequester),
                textStyle = textStyle(Color.White),
                value = value,
                onValueChange = {
                    if (it.length <= 6) onValueChange(it)
                },
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                )
            ) { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "*   *   *   *   *   *",
                        style = textStyle(Color.Gray)
                    )
                }
                innerTextField()
            }
        }
}

@Composable
@Preview
fun LoginScreenPreview() {
    Main()
}
