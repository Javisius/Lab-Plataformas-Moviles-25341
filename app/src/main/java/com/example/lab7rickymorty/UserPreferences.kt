package com.example.lab7rickymorty

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

private val nameKey = stringPreferencesKey("name")

class UserPreferences(private val context: Context) {

    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[nameKey] = name
        }
    }

    fun getName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[nameKey]
        }
    }

    suspend fun clearName() {
        context.dataStore.edit { preferences ->
            preferences.remove(nameKey)
        }
    }
}