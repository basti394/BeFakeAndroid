package pizza.xyz.befake.ui.screens

import android.view.inputmethod.EditorInfo
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
        LoginState.Error -> "Something went wrong. Please try again!"
    }

    val buttonText = when(loginState) {
        LoginState.PhoneNumber -> "Login"
        LoginState.OTPCode -> "Verify"
        LoginState.LoggedIn -> ""
        LoginState.Error -> "Verify Again"
    }

    val buttonEnabled = when(loginState) {
        LoginState.PhoneNumber -> phoneNumber.isNotEmpty()
        LoginState.OTPCode, LoginState.Error -> otpCode.length == 6
        LoginState.LoggedIn -> false
    }

    val onButtonClicked: () -> Unit = when(loginState) {
        LoginState.PhoneNumber -> viewModel::onLoginClicked
        LoginState.OTPCode -> viewModel::onVerifyClicked
        LoginState.LoggedIn -> fun() {}
        LoginState.Error -> {
            viewModel::onVerifyClicked
        }
    }

    if (loginState == LoginState.Error) {
        viewModel.onOptCodeChanged("") // Clear the OTP TextField
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
                color = if (loginState == LoginState.Error) Color.Red else Color.White,
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
            LoginState.OTPCode, LoginState.Error -> OTPCodeInput(
                value = otpCode,
                onValueChange = viewModel::onOptCodeChanged,
                focusRequester = focusRequester
            )
            LoginState.LoggedIn -> Spacer(modifier = Modifier)
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (loginState == LoginState.Error) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.5.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(50f)
                            )
                            .clip(RoundedCornerShape(50f))
                            .clickable { viewModel.onBackToPhoneNumberClicked() }
                            .height(45.dp)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Change Phone Number",
                            style = TextStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

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
                        .background(if (buttonEnabled) Color.White else Color.Gray)
                        .clickable(enabled = buttonEnabled) { onButtonClicked() }
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
}

val textStyle: (color: Color) -> TextStyle = { color ->
    TextStyle(
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
    )
}

@Composable
fun PhoneNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {

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
            ),
            decorationBox = {
                CodeInputDecoration(code = value)
            }
        )
    }
}

@Composable
private fun CodeInputDecoration(code: String) {
    Box(modifier = Modifier) {
        Row(horizontalArrangement = Arrangement.Center) {
            for (i in 0 until 6) {
                val text = if (i < code.length) code[i].toString() else ""
                CodeEntry(text)
            }
        }
    }
}

@Composable
private fun CodeEntry(text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(35.dp)
            .height(55.dp),
        contentAlignment = Alignment.Center
    ) {

        if (text == "") {
            Box {
                Canvas(
                    modifier = Modifier
                        .size(10.dp),
                    onDraw = {
                        drawCircle(Color.Gray)
                    }
                )
            }
        } else {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
            )
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreen(PaddingValues())
}
