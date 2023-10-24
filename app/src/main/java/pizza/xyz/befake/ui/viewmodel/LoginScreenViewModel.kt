package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.LoginRequestDTO
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginService: LoginService
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState.PhoneNumber)
    val loginState = _loginState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _optCode = MutableStateFlow("")
    val optCode = _optCode.asStateFlow()

    fun onPhoneNumberChanged(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun onOptCodeChanged(newOptCode: String) {
        _optCode.value = newOptCode
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            loginService.sendCode(LoginRequestDTO(phoneNumber.value))
        }
        _loginState.value = LoginState.OTPCode
    }

    fun onVerifyClicked() {
        viewModelScope.launch {
            //loginService.verifyCode(LoginRequestDTO(phoneNumber.value, optCode.value))
        }
        _loginState.value = LoginState.LoggedIn
    }
}

enum class LoginState {
    PhoneNumber,
    OTPCode,
    LoggedIn
}
