package com.group12.uistride.request;

public class UtilsApi {
    public static final String BASE_URL_API = "https://126c-66-96-225-134.ngrok-free.app";
    public static BaseApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

}