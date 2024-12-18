import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

val THEME_KEY = booleanPreferencesKey("dark_theme")

// Suspend function to save the theme preference
suspend fun saveThemePreference(context: Context, isDarkTheme: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[THEME_KEY] = isDarkTheme
    }
}

// Function to retrieve the theme preference
fun getThemePreference(context: Context) = context.dataStore.data
    .map { preferences -> preferences[THEME_KEY] ?: false }
