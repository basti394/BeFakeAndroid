package pizza.xyz.befake.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pizza.xyz.befake.Utils.TOKEN
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.dtos.login.LoginRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPRequestDTO
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginService: LoginService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState.PhoneNumber)
    val loginState = _loginState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _optCode = MutableStateFlow("")
    val optCode = _optCode.asStateFlow()

    private val _otpSession = MutableStateFlow("")

    fun onPhoneNumberChanged(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    init {
        runBlocking {
            val token = dataStore.data.first()[TOKEN]
            if (token?.isNotEmpty() == true) {
                _loginState.value = LoginState.LoggedIn
            }
        }
    }

    fun onOptCodeChanged(newOptCode: String) {
        _optCode.value = newOptCode
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            val res = loginService.sendCode(LoginRequestDTO(phoneNumber.value))
            _otpSession.value = res.getOrNull()?.data?.otpSession ?: ""
        }
        _loginState.value = LoginState.OTPCode
    }

    fun onVerifyClicked() {
        viewModelScope.launch {
            val res = loginService.verifyCode(VerifyOTPRequestDTO(_otpSession.value, optCode.value))
            if (res.isSuccess) {
                dataStore.edit { pref ->
                    pref[TOKEN] = res.getOrNull()?.data?.token ?: ""
                }
                _loginState.value = LoginState.LoggedIn
            } else if (res.isFailure) {
                _loginState.value = LoginState.Error
            }
        }
    }

    fun onBackToPhoneNumberClicked() {
        resetValues()
        _loginState.value = LoginState.PhoneNumber
    }

    private fun resetValues() {
        _phoneNumber.value = ""
        _optCode.value = ""
        _otpSession.value = ""
    }
}

enum class LoginState {
    PhoneNumber,
    OTPCode,
    LoggedIn,
    Error
}
