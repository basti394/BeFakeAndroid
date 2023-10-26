package pizza.xyz.befake

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object Utils {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
    val TOKEN = stringPreferencesKey("token")
    const val BASE_URL = "https://berealapi.fly.dev/"

}