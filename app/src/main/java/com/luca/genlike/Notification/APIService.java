package com.luca.genlike.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization:key=AAAAatjYnP8:APA91bGt5DnN2yxEh7qd1F1B3kh5gPzozG_u4o7bzocgllnsqQwaaifueBo3uh6mSeWL4bSu9w_DUb12B-qCLZP_RBylXUP9tYvqvsUDJN_Xlw9tBF4netWnnoYAf96IwX8iokgAGO2s"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
