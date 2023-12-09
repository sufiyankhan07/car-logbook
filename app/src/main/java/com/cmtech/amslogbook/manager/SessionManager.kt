package com.example.mypracticamstestko.Manager

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log

class SessionManager(private var _context: Context) {
    private var pref: SharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: Editor = pref.edit()

    // Shared pref mode
    internal var PRIVATE_MODE = 0

    val isLoggedIn: Boolean
        get() = pref.getBoolean(KEY_IS_LOGGED_IN, false)

    fun setLogin(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.commit()
        Log.d(TAG, "User login session modified!")
    }

    fun removePreference(key: String) {
        editor.remove(key)
        editor.commit()
    }

    companion object {
        private val TAG = SessionManager::class.java.simpleName
        // Shared preferences file name
        private val PREF_NAME = "Login"
        var KEY_USER_id = "user_id"

        private val KEY_IS_LOGGED_IN = "isLoggedIn"
    }

}
