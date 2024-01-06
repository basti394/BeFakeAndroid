package pizza.xyz.befake.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pizza.xyz.befake.utils.Utils

@Composable
fun BeFakeInputField(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onTrailingIconClick: () -> Unit = { },
    onChange: (String) -> Unit,
    onSubmit: (String) -> Unit = { },
    placeholder: String,
    clearValueOnSubmit: Boolean = false,
    focus: Boolean,
    initialValue: String = ""
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var active by remember { mutableStateOf(focus) }

    LaunchedEffect(active){
        if (active) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus(true)
        }
    }

    var inputValue by remember { mutableStateOf(initialValue) }

    LaunchedEffect(key1 = inputValue) {
        onChange(inputValue)
    }

    Row(
        modifier = modifier
            .height(50.dp)
            .background(Utils.lightBlack, RoundedCornerShape(cornerRadius.dp))
            .clip(RoundedCornerShape(cornerRadius))
            .clickable { active = true }
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
                if (!active) {
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
                        value = inputValue,
                        onValueChange = {
                            inputValue = it
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onSubmit(inputValue)
                                focusManager.clearFocus(true)
                                if (clearValueOnSubmit) {
                                    active = false
                                    inputValue = ""
                                }
                            }
                        )
                    )
                }
            }
            if (trailingIcon != null && active) {
                Box(
                    modifier = Modifier.clickable {
                        onTrailingIconClick()
                        inputValue = ""
                        active = false
                    }
                ) {
                    trailingIcon()
                }
            }
        }
    }
}

@Composable
@Preview
fun BeFakeInputFieldPreview() {
    BeFakeInputField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        leadingIcon = {},
        trailingIcon = {},
        onTrailingIconClick = { },
        onChange = { },
        onSubmit = { },
        placeholder = "Phone Number",
        clearValueOnSubmit = false,
        focus = true
    )
}