package com.benparvar.sousvide.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alans on 13/05/2017.
 */

public class Preferences {
    private final SharedPreferences mPreferences;
    private final Context mContext;

    public Preferences(Context context) {
        this.mPreferences = context.getSharedPreferences(Constants.SecurityKeys.PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.mContext = context;
    }


    @SuppressWarnings("SameParameterValue")
    public void removeStoredString(String key) {
        mPreferences.edit().remove(key).apply();
    }

    public void storeString(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public String getStoredString(String key) {
        return mPreferences.getString(key, "");
    }
}
