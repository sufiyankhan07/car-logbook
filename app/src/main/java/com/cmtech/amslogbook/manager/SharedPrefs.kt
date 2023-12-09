package com.example.mypracticamstestko.Manager

import android.content.Context
import android.content.SharedPreferences

public class SharedPrefs {
    private val TAG = SharedPrefs::class.java.simpleName
    //SharedPreferences file name

    companion object {
        var userid = "userid"
        var username = "user_name"
        var usersurname = "user_surname"
        var useremail = "user_email"
        var usercontact = "user_contact"
        var usertype = "user_type"
        var userpwd = "userpassword"
        var userno = "user_no"
        var SERVER_URL = "server_url"
        var ASSETS_COUNT = "assets_count"
        var UTime_zone = "UTime_zone"
        var slideType = "slideType"
        var SELECTED_GROUP_NAME:String = "selected_group_name_key"
        var SELECTED_GROUP_LABEL:String = "selected_group_label_key"
        var SELECED_VEHICLE_NAME:String="select_vehicle_name"
        var SELECTED_WJFILE_ID = "selected_wj_file_id"
        var SELECTED_WJFILE_NAME = "selected_wj_file_name"
        var isFirstTimeLogin = false
        private val SHARED_PREFS_FILE_NAME = "MyData"
        private fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
        }
        fun save(context: Context, key: String, value: String) {
            getPrefs(context).edit().putString(key, value).apply()
        }

        fun getString(context: Context, key: String): String? {
            return getPrefs(context).getString(key, "")
        }
    }

    //Save Booleans
    fun savePref(context: Context, key: String, value: Boolean) {
        getPrefs(context).edit().putBoolean(key, value).apply()
    }


    //Get Booleans
    fun getBoolean(context: Context, key: String): Boolean {
        return getPrefs(context).getBoolean(key, false)
    }

    //Get Booleans if not found return a predefined default value
    fun getBoolean(context: Context, key: String, defaultValue: Boolean): Boolean {
        return getPrefs(context).getBoolean(key, defaultValue)
    }

    //Strings

    fun getString(context: Context, key: String, defaultValue: String): String? {
        return getPrefs(context).getString(key, defaultValue)
    }

    //Integers
    fun save(context: Context, key: String, value: Int) {
        getPrefs(context).edit().putInt(key, value).apply()
    }

    fun getInt(context: Context, key: String): Int {
        return getPrefs(context).getInt(key, 0)
    }

    fun getInt(context: Context, key: String, defaultValue: Int): Int {
        return getPrefs(context).getInt(key, defaultValue)
    }

    //Floats
    fun save(context: Context, key: String, value: Float) {
        getPrefs(context).edit().putFloat(key, value).apply()
    }

    fun getFloat(context: Context, key: String): Float {
        return getPrefs(context).getFloat(key, 0f)
    }

    fun getFloat(context: Context, key: String, defaultValue: Float): Float {
        return getPrefs(context).getFloat(key, defaultValue)
    }

    //Longs
    fun save(context: Context, key: String, value: Long) {
        getPrefs(context).edit().putLong(key, value).apply()
    }

    fun getLong(context: Context, key: String): Long {
        return getPrefs(context).getLong(key, 0)
    }

    fun getLong(context: Context, key: String, defaultValue: Long): Long {
        return getPrefs(context).getLong(key, defaultValue)
    }

    //StringSets
    fun save(context: Context, key: String, value: Set<String>) {
        getPrefs(context).edit().putStringSet(key, value).apply()
    }

    fun getStringSet(context: Context, key: String): Set<String>? {
        return getPrefs(context).getStringSet(key, null)
    }

    fun getStringSet(context: Context, key: String, defaultValue: Set<String>): Set<String>? {
        return getPrefs(context).getStringSet(key, defaultValue)
    }

    fun remove(context: Context, key: String) {
        getPrefs(context).edit().remove(key).apply()
    }

    //label
}