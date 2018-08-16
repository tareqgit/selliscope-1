package com.humaclab.lalteer.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.humaclab.lalteer.activity.HomeActivity;
import com.humaclab.lalteer.activity.LoginActivity;
import com.humaclab.lalteer.model.UpdateProfile.UpdateProfileResponse;

import java.util.HashMap;

/**
 * Created by leon on 8/22/17.
 */


public class SessionManager {
    // Sharedpref file name
    private static final String PREF_NAME = "SelliscopePref";

    // All Shared Preferences Keys
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_DOB = "dob";
    private static final String KEY_USER_GENDER = "gender";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_USER_PROFILE_PIC_URL = "profilePictureUrl";
    private static final String KEY_FCM_TOKEN = "fcmToken";
    private static final String KEY_DIAMETER = "diameter";
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String IS_ALL_DATA_LOADADE = "IsAllDataLoaded";

    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Context
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String userName, String clientId, String userProfilePicUrl, String dob, String gender, String email, String password) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_DOB, dob);
        editor.putString(KEY_USER_GENDER, gender);
        editor.putString(KEY_CLIENT_ID, clientId);
        editor.putString(KEY_USER_PROFILE_PIC_URL, userProfilePicUrl);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.commit();
    }

    public void updateProfile(UpdateProfileResponse.User user) {
        editor.putString(KEY_USER_DOB, user.getDob());
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.putString(KEY_USER_PROFILE_PIC_URL, user.getImage());
    }

    public void setNewPassword(String password) {
        editor.putString(KEY_USER_PASSWORD, password);
        editor.commit();
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            Log.d("VALUE OF ISLOGGEDIN", "FALSE");
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        } else {
            Intent i = new Intent(_context,
                    HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
        user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));
        user.put(KEY_USER_DOB, pref.getString(KEY_USER_DOB, null));
        user.put(KEY_USER_GENDER, pref.getString(KEY_USER_GENDER, null));
        user.put(KEY_USER_PROFILE_PIC_URL, pref.getString(KEY_USER_PROFILE_PIC_URL, null));
        return user;
    }

    /**
     * @return userAuthorizationValue
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public String getUserPassword() {
        return pref.getString(KEY_USER_PASSWORD, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser(boolean hasUpdate) {
        editor.clear();
        editor.commit();
        if (!hasUpdate) {
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    /**
     * Quick check for login
     **/
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public String getFcmToken() {
        return pref.getString(KEY_FCM_TOKEN, "Sorry token has not been generated yet!");
    }

    public void setFcmToken(String fcmToken) {
        editor.putString(KEY_FCM_TOKEN, fcmToken);
    }

    public void setAllDataLoaded() {
        editor.putBoolean(IS_ALL_DATA_LOADADE, true);
        editor.commit();
    }

    public boolean isAllDataLoaded() {
        return pref.getBoolean(IS_ALL_DATA_LOADADE, false);
    }

    public Integer getDiameter() {
        return pref.getInt(KEY_DIAMETER, 0);
    }

    public void setDiameter(Integer diameter) {
        editor.putInt(KEY_DIAMETER, diameter);
        editor.commit();
    }
}
