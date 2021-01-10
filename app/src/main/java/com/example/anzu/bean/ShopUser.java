package com.example.anzu.bean;

import java.sql.Timestamp;

public class ShopUser {
    private int id;
    private String uid;
    private String cellphone;
    private String password;
//    private Timestamp registerTime;
//    private Timestamp lastLoginTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public Timestamp getRegisterTime() {
//        return registerTime;
//    }
//
//    public void setRegisterTime(Timestamp registerTime) {
//        this.registerTime = registerTime;
//    }
//
//    public Timestamp getLastLoginTime() {
//        return lastLoginTime;
//    }
//
//    public void setLastLoginTime(Timestamp lastLoginTime) {
//        this.lastLoginTime = lastLoginTime;
//    }
}
