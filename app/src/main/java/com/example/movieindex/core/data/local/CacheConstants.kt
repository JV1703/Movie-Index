package com.example.movieindex.core.data.local

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object CacheConstants {

    const val DATABASE_NAME = "movie_database"

    // datastore - shared preference
    const val AUTH_PREFERENCES_NAME = "auth_preferences"
    val SESSION_ID = stringPreferencesKey("session_id")
//    val ACCOUNT_ID = intPreferencesKey("account_id")
    val CASTS = stringPreferencesKey("castList")
    val CREWS = stringPreferencesKey("crewList")

}