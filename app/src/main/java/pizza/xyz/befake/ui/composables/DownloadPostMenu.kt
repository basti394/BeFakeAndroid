package pizza.xyz.befake.ui.composables

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import pizza.xyz.befake.utils.Utils

@Composable
fun DownloadPostMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    btsLink: String?,
    primaryLink: String,
    secondaryLink: String,
    takenAt: String,
    userName: String
) {
    val context = LocalContext.current

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color(0xFF131313))
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "Download Selfie",
                    color = Color.White
                )
            },
            onClick = {
                Utils.download(secondaryLink, "SELFIE_${userName}_${takenAt}.webp", context)
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        )
        Divider(color = Color.DarkGray)
        DropdownMenuItem(
            text = {
                Text(
                    text = "Download Front",
                    color = Color.White
                )
            },
            onClick = { Utils.download(primaryLink, "FRONT_${userName}_${takenAt}.webp", context)
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        )
        if (!btsLink.isNullOrBlank()) {
            Divider(color = Color.DarkGray)
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Download BTS",
                        color = Color.White
                    )
                },
                onClick = { Utils.download(btsLink, "BTS_${userName}_${takenAt}.mp4", context)
                    onDismissRequest()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        }
    }
}