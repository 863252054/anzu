package com.example.anzu.bean;

import java.io.Serializable;

/**
 * @param <T>
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msg;
    private int code;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
