package com.miracle.monke.data.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.miracle.monke.domain.authorization.preferences.AuthorizationPreferncesProvier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SharedPrefs(private val context: Context):
    AuthorizationPreferncesProvier {

    companion object {
        private const val PREF = "MonkeDemo"
        private const val AUTHORIZATION = "authorization"
    }

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }

    private fun remove(key: String) {
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear() {
        sharedPref.edit().clear().apply()
    }

    private fun createListener(preferenceKey: String, onPreferenceChange:()->Unit): SharedPreferences. OnSharedPreferenceChangeListener{
        return SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == preferenceKey) {
                onPreferenceChange()
            }
        }
    }

    override fun saveAuthorization(authorization: Boolean) {
        put(AUTHORIZATION,authorization)
    }

    override fun getAuthorization(): Boolean {
        return sharedPref.getBoolean(AUTHORIZATION, false)
    }

    override suspend fun getAuthorizationFlow(): Flow<Boolean> = callbackFlow {
        val listener = createListener(AUTHORIZATION){
            trySend(getAuthorization())
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        send(getAuthorization())
        awaitClose {
            sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}


