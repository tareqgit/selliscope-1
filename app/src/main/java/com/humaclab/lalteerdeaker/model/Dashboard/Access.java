package com.humaclab.lalteerdeaker.model.Dashboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Access extends RealmObject {

    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("access")
    private Integer access;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

}