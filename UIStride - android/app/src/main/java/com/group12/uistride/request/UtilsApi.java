package com.group12.uistride.request;

public class UtilsApi {
    public static final String BASE_URL_API = "https://8dca-182-3-44-253.ngrok-free.app";
    public static BaseApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

}