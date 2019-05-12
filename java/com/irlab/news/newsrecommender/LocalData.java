package com.irlab.news.newsrecommender;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocalData {

    private static final String APP_SHARED_PREFS = "RemindMePref";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private static final String reminderStatus="reminderStatus";
    private static final String hour="hour";
    private static final String min="min";
    private static final String username = "username";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public LocalData(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public boolean getReminderStatus()
    {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setUsername(String user_name){
        prefsEditor.putString(username,user_name);
        prefsEditor.apply();
        prefsEditor.commit();
    }


    public String getUsername(){
        return appSharedPrefs.getString(username,"");
    }

    public void setReminderStatus(boolean status)
    {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Hour)

    public int get_hour()
    {
        return appSharedPrefs.getInt(hour, 8);
    }

    public void set_hour(int h)
    {
        prefsEditor.putInt(hour, h);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Minutes)

    public int get_min()
    {
        return appSharedPrefs.getInt(min, 0);
    }

    public void set_min(int m)
    {
        prefsEditor.putInt(min, m);
        prefsEditor.commit();
    }

    public void reset()
    {
        prefsEditor.clear();
        prefsEditor.commit();

    }
    public static void setDefaultUser(String key, String value, Context context) {
        preferences = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaultUser() {
        String user;
        try{
            user = preferences.getString("username","");
        }
        catch(NullPointerException e){
            user = "default";
        }
        return user;
    }
}
