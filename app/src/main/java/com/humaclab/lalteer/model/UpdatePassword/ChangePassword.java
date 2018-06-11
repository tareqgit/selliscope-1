package com.humaclab.lalteer.model.UpdatePassword;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 8/3/18.
 */

public class ChangePassword implements Serializable {
    @SerializedName("current_password")
    private String currentPassword;

    @SerializedName("new_password")
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
