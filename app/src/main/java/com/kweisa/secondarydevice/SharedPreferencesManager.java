package com.kweisa.secondarydevice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

class SharedPreferencesManager {
    private static final String DEFAULT = "Default";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferencesManager() {
    }

    private static class Singleton {
        private static final SharedPreferencesManager instance = new SharedPreferencesManager();
    }

    static SharedPreferencesManager getInstance() {
        return Singleton.instance;
    }

    @SuppressLint("CommitPrefEdits")
    void load(Context context) {
        sharedPreferences = context.getSharedPreferences(DEFAULT, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    SharedPreferences.Editor getEditor() {
        return editor;
    }
}
