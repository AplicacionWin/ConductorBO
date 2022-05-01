package com.nikola.driver.network.newnetwork;

import com.nikola.driver.network.newnetwork.APIConstants.ErrorCodes;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIConstants.URLs;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class APIClient {

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    ResponseBody responseBody = response.body();
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();
                    String respData = buffer.clone().readString(Charset.defaultCharset());
                    JSONObject resp = null;
                    try {
                        resp = new JSONObject(respData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (resp != null) {
                        switch (resp.optInt(Params.ERROR_CODE)) {
                            case ErrorCodes.TOKEN_EXPIRED:
                                emitEvent(ErrorCodes.TOKEN_EXPIRED, "Token expired, Please login again!");
                                break;

                            case ErrorCodes.INVALID_TOKEN:
                                emitEvent(ErrorCodes.INVALID_TOKEN, "Invalid token, Please login again!");
                                break;

                            case ErrorCodes.USER_DOESNT_EXIST:
                                emitEvent(ErrorCodes.USER_DOESNT_EXIST, "User does not exist in the database.");
                                break;

                            case ErrorCodes.ID_OR_TOKEN_MISSING:
                                emitEvent(ErrorCodes.ID_OR_TOKEN_MISSING, "Id/Token missing. we have been notified!");
                                break;

                            case ErrorCodes.VERIFY_USER:
                                emitEvent(ErrorCodes.VERIFY_USER, "Your account is not been approved by the admin");
                                break;
                        }
                    }
                    return response;
                }).build();

        return new Retrofit.Builder()
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
    }

    private static void emitEvent(int code, String message) {
        EventBus.getDefault().post(new APIEvent(message, code));
    }
}
