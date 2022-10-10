package com.gps.classattendanceapp.components

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val context: Context
){
    private val Context.datastore by preferencesDataStore("UserPreferences")
    private val prominentDisclosureKey = booleanPreferencesKey("prominentDisclosure")

    suspend fun showProminentDisclosure():Boolean{
         return context.datastore.data.map{pref-> pref[prominentDisclosureKey] }.first() ?: true
    }

    suspend fun showedProminentDisclosure(){
        context.datastore.edit{ pref->
            pref[prominentDisclosureKey] = false
        }
    }
}