package pizza.xyz.befake.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import pizza.xyz.befake.R
import pizza.xyz.befake.model.dtos.countrycode.Country
import pizza.xyz.befake.ui.screens.textStyle
import pizza.xyz.befake.ui.viewmodel.CountryCodeSelectionSheetViewModel
import pizza.xyz.befake.utils.Utils.debugPlaceholder
import pizza.xyz.befake.utils.Utils.flagType


@Composable
fun CountryCodeSelectionSheet(
    viewModel: CountryCodeSelectionSheetViewModel = hiltViewModel(),
    onCountrySelected: (Country) -> Unit,
    currentCountry: Country,
    onDismiss: () -> Unit
) {

    val countries by viewModel.countries.collectAsStateWithLifecycle()

    val onSearch = { search: String ->
        viewModel.searchCountry(search)
    }

    CountryCodeSelectionSheetContent(
        countries = countries,
        currentCountry = currentCountry,
        onDismiss = onDismiss,
        onCountrySelected = onCountrySelected,
        onSearch = onSearch
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CountryCodeSelectionSheetContent(
    countries: List<Country>,
    currentCountry: Country,
    onDismiss: () -> Unit,
    onCountrySelected: (Country) -> Unit,
    onSearch: (String) -> Unit
) {

    val groupedCountries = countries.groupBy { it.name.first().uppercaseChar() }

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.choose_country),
                            style = TextStyle(
                                lineBreak = LineBreak.Simple,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "close",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black)
                )
                BeFakeInputField(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close",
                            tint = Color.White
                        )
                    },
                    onChange = onSearch,
                    placeholder = stringResource(R.string.search_for_your_country),
                    focus = false,
                    initialValue = ""
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {

                    groupedCountries.forEach { (letter, countriesInGroup) ->
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(Color.Black)
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    text = "$letter.",
                                    style = textStyle(Color.White)
                                )
                            }
                        }
                        items(countriesInGroup) { country ->
                            CountryCodeItem(
                                modifier = Modifier
                                    .padding(8.dp),
                                country = country,
                                selected = currentCountry.code == country.code,
                                onCountrySelected = onCountrySelected
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountryCodeItem(
    modifier: Modifier = Modifier,
    country: Country,
    selected: Boolean,
    onCountrySelected: (Country) -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onCountrySelected(country) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(30.dp),
                    model = "https://flagsapi.com/${country.code}/$flagType/64.png",
                    contentDescription = "flag",
                    placeholder = debugPlaceholder(id = R.drawable.country_example),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${country.name} (${country.dialCode})",
                    style = TextStyle(
                        lineBreak = LineBreak.Simple,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }

            if (selected) {
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(20.dp),
                    imageVector = Icons.Filled.Check,
                    contentDescription = "selected",
                    tint = Color.White
                )
            }

        }
        Divider(
            color = Color.DarkGray ,
            thickness = 1.dp
        )
    }

}

@Composable
@Preview
fun CountryCodeSelectionSheetPreview() {
    val countries = listOf(
        Country("Österreich", "+43", "AT"),
        Country("Schweiz", "+41", "CH"),
        Country("Frankreich", "+33", "FR"),
        Country("Italien", "+39", "IT"),
        Country("Spanien", "+34", "ES"),
        Country("Deutschland", "+49", "DE"),
        Country("Polen", "+48", "PL"),
        Country("Niederlande", "+31", "NL"),
        Country("Belgien", "+32", "BE"),
        Country("Dänemark", "+45", "DK"),
        Country("Tschechien", "+420", "CZ"),
        Country("Slowakei", "+421", "SK"),
        Country("Ungarn", "+36", "HU"),
        Country("Slowenien", "+386", "SI"),
        Country("Kroatien", "+385", "HR"),
        Country("Bosnien und Herzegowina", "+387", "BA"),
        Country("Serbien", "+381", "RS"),
        Country("Montenegro", "+382", "ME"),
        Country("Albanien", "+355", "AL"),
    )
    val currentCountry = Country("Deutschland", "+49", "DE")


    CountryCodeSelectionSheetContent(
        countries.sortedBy { it.name.first() },
        currentCountry,
        {},
        {},
        {}
    )
}