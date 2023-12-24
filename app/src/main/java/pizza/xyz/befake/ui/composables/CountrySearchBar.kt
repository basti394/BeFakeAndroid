package pizza.xyz.befake.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CountrySearchBar(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onTrailingIconClick: () -> Unit = { },
    onSearch: (String) -> Unit,
    placeholder: String,
) {

    val focusRequester = remember { FocusRequester() }

    var searching by remember { mutableStateOf(false) }

    LaunchedEffect(searching){
        if (searching) {
            focusRequester.requestFocus()
        }
    }

    var searchValue by remember { mutableStateOf("") }

    LaunchedEffect(key1 = searchValue) {
        onSearch(searchValue)
    }

    Box(
        modifier = modifier
            .clickable { searching = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                if (!searching) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        style = TextStyle(fontSize = 18.sp)
                    )
                } else {
                    BasicTextField(
                        textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.White),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(horizontal = 8.dp),
                        value = searchValue,
                        onValueChange = {
                            searchValue = it
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White)
                    )
                }
            }
            if (trailingIcon != null && searching) {
                Box(
                    modifier = Modifier.clickable {
                        onTrailingIconClick()
                        searchValue = ""
                        searching = false
                    }
                ) {
                    trailingIcon()
                }
            }
        }
    }
}