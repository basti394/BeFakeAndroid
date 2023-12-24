package pizza.xyz.befake.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pizza.xyz.befake.model.dtos.countrycode.Country
import pizza.xyz.befake.utils.Utils
import javax.inject.Inject

@HiltViewModel
class CountryCodeSelectionSheetViewModel @Inject constructor(

) : ViewModel() {

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries = _countries.asStateFlow()

    private val allCountries = Utils.getCountries()

    init {
        getCountries()
    }

    private fun getCountries() {
        _countries.value = Utils.getCountries()
    }

    fun searchCountry(value: String) {
        val prompt = value.lowercase()
        if (prompt.isEmpty()) {
            _countries.value = allCountries
            return
        }
        if (prompt.toDoubleOrNull() != null || prompt.startsWith("+")) {
            searchNumber(prompt)
        } else {
            searchName(prompt)
        }
    }

    private fun searchNumber(value: String) {
        _countries.value = allCountries.filter { it.dialCode.contains(value) }
    }

    private fun searchName(value: String) {
        _countries.value = allCountries.filter { it.name.lowercase().contains(value) }
    }
}