package pizza.xyz.befake.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pizza.xyz.befake.R
import pizza.xyz.befake.Utils.TOKEN
import pizza.xyz.befake.data.LoginService
import pizza.xyz.befake.model.countrycode.Country
import pizza.xyz.befake.model.dtos.login.LoginRequestDTO
import pizza.xyz.befake.model.dtos.verify.VerifyOTPRequestDTO
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginService: LoginService,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.PhoneNumber)
    val loginState = _loginState.asStateFlow()

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
            val checkToken = async {
                val token = dataStore.data.first()[TOKEN]
                if (token?.isNotEmpty() == true) {
                    _loginState.value = LoginState.LoggedIn
                }
            }
            checkToken.await()

            val getDefaultCountry = async {
                _country.value = Country("Deutschland", "+49", "DE")
            }
            getDefaultCountry.await()
        }
    }

    fun onCountryChanged(newCountry: Country) {
        _country.value = newCountry
    }

    fun onOptCodeChanged(newOptCode: String) {
        _optCode.value = newOptCode
    }

    fun onLoginClicked() {
        _loginState.value = LoginState.Loading(LoginState.PhoneNumber)
        val phoneNumberWithCountry = "${country.value.dialCode}${phoneNumber.value}"
        if (!phoneNumberWithCountry.startsWith("+")) {
            _loginState.value = LoginState.Error(LoginState.PhoneNumber, R.string.phone_numer_start_with_plus)
            return
        }
        viewModelScope.launch {
            val res = loginService.sendCode(LoginRequestDTO(phoneNumberWithCountry))
            if (res.isSuccess){
                _otpSession.value = res.getOrNull()?.data?.otpSession ?: ""
                _loginState.value = LoginState.OTPCode
            } else if (res.isFailure) {
                _loginState.value = LoginState.Error(
                    LoginState.PhoneNumber,
                    R.string.something_went_wrong_please_try_again,
                    res.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onVerifyClicked() {

        viewModelScope.launch {
            _loginState.value = LoginState.Loading(LoginState.OTPCode)
            val res = loginService.verifyCode(VerifyOTPRequestDTO(_otpSession.value, optCode.value))
            if (res.isSuccess) {
                dataStore.edit { pref ->
                    pref[TOKEN] = res.getOrNull()?.data?.token ?: ""
                }
                _loginState.value = LoginState.LoggedIn
            } else if (res.isFailure) {
                _loginState.value = LoginState.Error(LoginState.OTPCode, R.string.something_went_wrong_please_try_again, res.exceptionOrNull()?.message)
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
