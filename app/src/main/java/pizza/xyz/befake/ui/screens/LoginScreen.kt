package pizza.xyz.befake.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.countrycode.Country
import pizza.xyz.befake.ui.composables.CountryCodeSelectionSheet
import pizza.xyz.befake.ui.viewmodel.LoginScreenViewModel
import pizza.xyz.befake.ui.viewmodel.LoginState
import pizza.xyz.befake.utils.Utils.debugPlaceholder
import pizza.xyz.befake.utils.Utils.flagType

const val cornerRadius = 40f

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val otpCode by viewModel.optCode.collectAsStateWithLifecycle()
    val currentCountry by viewModel.country.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    val headerText = when(loginState) {
        is LoginState.PhoneNumber -> stringResource(R.string.phone_number_header)
        is LoginState.OTPCode -> stringResource(R.string.otp_code_header, "${currentCountry.dialCode} $phoneNumber")
        is LoginState.LoggedIn -> ""
        is LoginState.Loading -> {
            when ((loginState as LoginState.Loading).previousState) {
                is LoginState.PhoneNumber -> stringResource(R.string.phone_number_header)
                is LoginState.OTPCode -> stringResource(R.string.otp_code_header)
                else -> ""
            }
        }
        is LoginState.Error -> {
            val tempState = loginState as LoginState.Error
            if (tempState.message != null) {
                stringResource(tempState.messageResource, tempState.message)
            } else {
                stringResource(tempState.messageResource)
            }
        }
    }

    val buttonText = when(loginState) {
        is LoginState.PhoneNumber -> stringResource(R.string.login)
        is LoginState.OTPCode -> stringResource(R.string.verify)
        is LoginState.LoggedIn -> ""
        is LoginState.Error -> stringResource(R.string.try_again)
        is LoginState.Loading -> ""
    }

    val buttonEnabled = when(loginState) {
        is LoginState.PhoneNumber -> phoneNumber.isNotEmpty()
        is LoginState.OTPCode -> otpCode.length == 6
        is LoginState.Error -> {
            if ((loginState as LoginState.Error).previousState == LoginState.PhoneNumber) phoneNumber.isNotEmpty()
            else otpCode.length == 6
        }
        is LoginState.LoggedIn -> false
        is LoginState.Loading -> true
    }

    val onButtonClicked: () -> Unit = when(loginState) {
        is LoginState.PhoneNumber -> viewModel::onLoginClicked
        is LoginState.OTPCode -> viewModel::onVerifyClicked
        is LoginState.LoggedIn -> fun() {}
        is LoginState.Loading -> fun() {}
        is LoginState.Error -> {
            when((loginState as LoginState.Error).previousState) {
                is LoginState.PhoneNumber -> viewModel::onLoginClicked
                is LoginState.OTPCode -> viewModel::onVerifyClicked
                else -> fun() {}
            }
        }
    }

    LoginScreenContent(
        loginState = loginState,
        phoneNumber = phoneNumber,
        otpCode = otpCode,
        currentCountry = currentCountry,
        focusRequester = focusRequester,
        paddingValues = paddingValues,
        headerText = headerText,
        buttonText = buttonText,
        buttonEnabled = buttonEnabled,
        onButtonClicked = onButtonClicked,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onOptCodeChanged = viewModel::onOptCodeChanged,
        onBackToPhoneNumberClicked = viewModel::onBackToPhoneNumberClicked,
        onCountrySelected = viewModel::onCountryChanged
    )
}

@Composable
fun LoginScreenContent(
    loginState: LoginState,
    phoneNumber: String,
    otpCode: String,
    currentCountry: Country,
    focusRequester: FocusRequester,
    paddingValues: PaddingValues,
    headerText: String,
    buttonText: String,
    buttonEnabled: Boolean,
    onButtonClicked: () -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onOptCodeChanged: (String) -> Unit,
    onBackToPhoneNumberClicked: () -> Unit,
    onCountrySelected: (Country) -> Unit
) {

    BackHandler(
        loginState == LoginState.OTPCode ||
                loginState is LoginState.Error &&
                loginState.previousState is LoginState.OTPCode
    ) {
        onBackToPhoneNumberClicked()
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
            textAlign = TextAlign.Center,
            style = TextStyle(
                lineBreak = LineBreak.Simple,
                color = if (loginState is LoginState.Error) Color.Red else Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        when(loginState) {
            is LoginState.PhoneNumber -> PhoneNumberInput(
                value = phoneNumber,
                onValueChange = onPhoneNumberChanged,
                focusRequester = focusRequester,
                onCountrySelected = onCountrySelected,
                currentCountry = currentCountry,
                send = onButtonClicked,
            )
            is LoginState.OTPCode -> OTPCodeInput(
                value = otpCode,
                onValueChange = onOptCodeChanged,
                focusRequester = focusRequester,
                onSubmit = onButtonClicked,
            )
            is LoginState.Error, is LoginState.Loading -> {
                if ((loginState as LoginState.LoginStateWithPreviousState).previousState is LoginState.PhoneNumber) {
                    PhoneNumberInput(
                        value = phoneNumber,
                        onValueChange = onPhoneNumberChanged,
                        focusRequester = null,
                        onCountrySelected = onCountrySelected,
                        currentCountry = currentCountry,
                        send = onButtonClicked,
                    )
                } else if (loginState.previousState is LoginState.OTPCode) {
                    OTPCodeInput(
                        value = otpCode,
                        onValueChange = onOptCodeChanged,
                        focusRequester = null,
                        onSubmit = onButtonClicked,
                    )
                }
            }
            is LoginState.LoggedIn -> Spacer(modifier = Modifier)
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (loginState is LoginState.Error && loginState.previousState is LoginState.OTPCode) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .clip(RoundedCornerShape(cornerRadius))
                            .clickable { onBackToPhoneNumberClicked() }
                            .height(45.dp)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.change_phone_number),
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
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(if (buttonEnabled) Color.White else Color.Gray)
                        .clickable(enabled = buttonEnabled) { onButtonClicked() }
                        .height(45.dp)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(25.dp),
                            color = Color.Black,
                            strokeWidth = 3.dp
                        )
                    } else {
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
    focusRequester: FocusRequester?,
    onCountrySelected: (Country) -> Unit,
    currentCountry: Country,
    send: () -> Unit,
) {

    val textSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.White,
    )
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        focusRequester?.requestFocus()
    }

    if (expanded) {
        CountryCodeSelectionSheet(
            onCountrySelected = {
                onCountrySelected(it)
                expanded = false
                focusRequester?.requestFocus()
            },
            currentCountry = currentCountry,
            onDismiss = {
                expanded = false
                focusRequester?.requestFocus()
            }
        )
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CountryCodeSelector(
                openDialog = { expanded = true },
                currentCountry = currentCountry
            )
            BasicTextField(
                singleLine = true,
                modifier = focusRequester?.let {
                    Modifier
                        .focusRequester(it)
                } ?: Modifier,
                textStyle = textStyle(Color.White),
                value = value,
                onValueChange = {
                    if (it.length <= 15) onValueChange(it)
                },
                cursorBrush = SolidColor(Color.White),
                keyboardActions = KeyboardActions(
                    onDone = {
                        send()
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                )
            ) { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.phone_number),
                        style = textStyle(Color.Gray)
                    )
                }
                innerTextField()
            }
        }

    }
}

@Composable
fun CountryCodeSelector(
    openDialog: () -> Unit,
    currentCountry: Country
) {
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .clickable { openDialog() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp),
                model = "https://flagsapi.com/${currentCountry.code}/$flagType/64.png",
                contentDescription = "flag",
                placeholder = debugPlaceholder(id = R.drawable.country_example),
            )
            Text(
                text = currentCountry.dialCode,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                ),
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun OTPCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester?,
    onSubmit: () -> Unit,
) {

    val textSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.White,
    )
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(key1 = Unit) {
        focusRequester?.requestFocus()
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        BasicTextField(
            singleLine = true,
            modifier = focusRequester?.let {
                Modifier
                    .focusRequester(it)

            } ?: Modifier,
            interactionSource = interactionSource,
            textStyle = textStyle(Color.White),
            value = value,
            onValueChange = {
                if (it.length <= 6) onValueChange(it)
                if (it.length == 6) {
                    onSubmit()
                    focusManager.clearFocus(true)
                }
            },
            cursorBrush = SolidColor(Color.White),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit()
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            decorationBox = {
                CodeInputDecoration(code = value, isFocused)
            }
        )
    }
}

@Composable
private fun CodeInputDecoration(
    code: String, isFocused: Boolean
) {
    Box(modifier = Modifier) {
        Row(horizontalArrangement = Arrangement.Center) {
            for (i in 0 until 6) {
                val prevText = if (i > 0) (code.getOrNull(i - 1) ?: "").toString() else ""
                val text = if (i < code.length) code[i].toString() else ""
                val blink = isFocused && text.isBlank() && (prevText.isNotBlank() || i == 0)
                CodeEntry(text, blink)
            }
        }
    }
}

@Composable
private fun CodeEntry(text: String, isFocused: Boolean) {

    var blinkColor by remember {
        mutableStateOf(Color.Gray)
    }

    LaunchedEffect(key1 = isFocused) {
        if (isFocused) {
            while (true) {
                blinkColor = Color.White
                delay(500)
                blinkColor = Color.Gray
                delay(500)
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(35.dp)
            .height(55.dp),
        contentAlignment = Alignment.Center
    ) {

        if (text == "") {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(10.dp)
                    .background(blinkColor)
            ) { }
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
fun LoginScreenPhoneNumberPreview() {
    LoginScreenContent(
        loginState = LoginState.PhoneNumber,
        phoneNumber = "",
        otpCode = "",
        focusRequester = FocusRequester(),
        paddingValues = PaddingValues(),
        headerText = stringResource(R.string.phone_number_header),
        buttonText = stringResource(R.string.login),
        buttonEnabled = false,
        onButtonClicked = {},
        onPhoneNumberChanged = {},
        onOptCodeChanged = {},
        onBackToPhoneNumberClicked = {},
        onCountrySelected = {},
        currentCountry = Country("Germany", "+49", "DE")

    )
}

@Composable
@Preview
fun LoginScreenErrorPhoneNumberPreview() {
    LoginScreenContent(
        loginState = LoginState.Error(previousState = LoginState.PhoneNumber, message = "400", messageResource = R.string.something_went_wrong_please_try_again),
        phoneNumber = "",
        otpCode = "",
        focusRequester = FocusRequester(),
        paddingValues = PaddingValues(),
        headerText = stringResource(R.string.phone_number_header),
        buttonText = stringResource(R.string.try_again),
        buttonEnabled = false,
        onButtonClicked = {},
        onPhoneNumberChanged = {},
        onOptCodeChanged = {},
        onBackToPhoneNumberClicked = {},
        onCountrySelected = {},
        currentCountry = Country("Germany", "+49", "DE")
    )
}

@Composable
@Preview
fun LoginScreenOTPCodePreview() {
    LoginScreenContent(
        loginState = LoginState.OTPCode,
        phoneNumber = "",
        otpCode = "",
        focusRequester = FocusRequester(),
        paddingValues = PaddingValues(),
        headerText = stringResource(R.string.otp_code_header),
        buttonText = stringResource(R.string.verify),
        buttonEnabled = false,
        onButtonClicked = {},
        onPhoneNumberChanged = {},
        onOptCodeChanged = {},
        onBackToPhoneNumberClicked = {},
        onCountrySelected = {},
        currentCountry = Country("Germany", "+49", "DE")
    )
}

@Composable
@Preview
fun LoginScreenErrorOTPCodePreview() {
    LoginScreenContent(
        loginState = LoginState.Error(previousState = LoginState.OTPCode, message = "400", messageResource = R.string.something_went_wrong_please_try_again),
        phoneNumber = "",
        otpCode = "",
        focusRequester = FocusRequester(),
        paddingValues = PaddingValues(),
        headerText = stringResource(R.string.otp_code_header),
        buttonText = stringResource(R.string.try_again),
        buttonEnabled = false,
        onButtonClicked = {},
        onPhoneNumberChanged = {},
        onOptCodeChanged = {},
        onBackToPhoneNumberClicked = {},
        onCountrySelected = {},
        currentCountry = Country("Germany", "+49", "DE")
    )
}