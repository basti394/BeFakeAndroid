
package pizza.xyz.befake.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pizza.xyz.befake.ui.composables.Post
import pizza.xyz.befake.ui.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {

    val feed = homeScreenViewModel.feed.collectAsStateWithLifecycle()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val count = feed.value?.data?.data?.friendsPosts?.size ?: 3

        items(count) {
            if (it == 0) Spacer(modifier = Modifier.height(130.dp))
            Post(
                post = feed.value?.data?.data?.friendsPosts?.reversed()?.get(it),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}