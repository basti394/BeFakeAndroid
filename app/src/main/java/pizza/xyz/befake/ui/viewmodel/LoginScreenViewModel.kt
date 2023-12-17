package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pizza.xyz.befake.R
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.countrycode.Country
import pizza.xyz.befake.model.dtos.login.LoginRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPRequestDTO
import pizza.xyz.befake.utils.Utils.getCountries
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginService: LoginService,
) : ViewModel() {

    val loginState = loginService.loginState

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _country = MutableStateFlow(Country("", "", ""))
    val country = _country.asStateFlow()

    private val _optCode = MutableStateFlow("")
    val optCode = _optCode.asStateFlow()

    private val _otpSession = MutableStateFlow("")

    fun onPhoneNumberChanged(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    init {
        runBlocking(Dispatchers.IO) {
            loginService.checkIfLoggedIn()
            setDefaultCountry()
        }
    }

    private fun setDefaultCountry() {
        val dialCode = getCountries().find { it.code == Locale.getDefault().country }?.dialCode ?: "+49"
        _country.value = Country(Locale.getDefault().displayCountry, dialCode, Locale.getDefault().country)
    }

    fun onCountryChanged(newCountry: Country) {
        _country.value = newCountry
    }

    fun onOptCodeChanged(newOptCode: String) {
        _optCode.value = newOptCode
    }

    fun onLoginClicked() {
        val phoneNumberWithCountry = "${country.value.dialCode}${phoneNumber.value}"
        viewModelScope.launch {
            loginService.sendCode(LoginRequestDTO(phoneNumberWithCountry)).onSuccess {
                _otpSession.value = it.data?.otpSession ?: ""
            }.onFailure {
                if (it.message != "Invalid phone number") {
                    loginService.setLoginState(LoginState.Error(
                        LoginState.PhoneNumber,
                        R.string.something_went_wrong_please_try_again,
                        it.message
                    ))
                }
            }
        }
    }

    fun onVerifyClicked() {
        viewModelScope.launch {
            loginService.verifyCode(VerifyOTPRequestDTO(_otpSession.value, optCode.value)).onFailure {
                _optCode.value = ""
                loginService.setLoginState(LoginState.Error(LoginState.OTPCode, R.string.something_went_wrong_please_try_again, it.message))
            }
        }
    }

    fun onBackToPhoneNumberClicked() {
        resetValues()
        loginService.setLoginState(LoginState.PhoneNumber)
    }

    private fun resetValues() {
        _phoneNumber.value = ""
        _optCode.value = ""
        _otpSession.value = ""
    }
}

sealed class LoginState {
    object PhoneNumber : LoginState()
    object OTPCode : LoginState()
    object LoggedIn : LoginState()

    sealed class LoginStateWithPreviousState : LoginState() {
        abstract val previousState: LoginState
    }

    class Loading(override val previousState: LoginState) : LoginStateWithPreviousState()
    class Error(override val previousState: LoginState, val messageResource: Int, val message: String? = null) : LoginStateWithPreviousState()
}
