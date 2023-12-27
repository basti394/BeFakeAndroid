package pizza.xyz.befake.ui.composables

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import pizza.xyz.befake.R
import pizza.xyz.befake.utils.Utils

@Composable
fun ProfilePicture(
    modifier: Modifier,
    profilePicture: String?,
    username: String
) {
    AsyncImage(
        modifier = modifier.clip(CircleShape),
        placeholder = Utils.debugPlaceholderProfilePicture(id = R.drawable.profile_picture_example),
        model = getProfilePictureUrl(profilePicture, username),
        contentDescription = "profilePicture"
    )
}

private fun getProfilePictureUrl(profilePicture: String?, username: String): String {
    return profilePicture
        ?: if (username.isEmpty()) {
            "https://ui-avatars.com/api/?name=&background=8B8B8B&size=100"
        } else {
            "https://ui-avatars.com/api/?name=${username.first()}&background=random&size=100"
        }
}
