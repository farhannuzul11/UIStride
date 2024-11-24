package com.group12.uistride.model;

public class BaseResponse<T> {
    public boolean success;
    public String message;
    public T payload;
}
