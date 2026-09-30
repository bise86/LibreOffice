/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
package org.libreoffice;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import java.util.Locale;

/** Applies the optional in-app language override while preserving system default behavior. */
public final class LocaleHelper {
    public static final String LANGUAGE_PREFS_KEY = "APP_LANGUAGE";
    public static final String SYSTEM_LANGUAGE = "system";

    private LocaleHelper() {
    }

    public static Context wrap(Context context) {
        String language = getLanguage(context);
        if (SYSTEM_LANGUAGE.equals(language) || language.isEmpty()) {
            return context;
        }

        Locale locale = Locale.forLanguageTag(language);
        if (locale.equals(Locale.getDefault())) {
            return context;
        }

        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public static void setLanguage(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .edit().putString(LANGUAGE_PREFS_KEY, language).apply();
    }

    private static String getLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext());
        return preferences.getString(LANGUAGE_PREFS_KEY, SYSTEM_LANGUAGE);
    }
}
