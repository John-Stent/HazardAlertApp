package com.hazardalert.app.az.SendNotificationPack;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfqBxF1M:APA91bHyQ6ToL1nzRwbQSYyYN0OtrXl6fowpiHDVzDbnEafhkDNdsl4uNDIXf_9fcSBl4ZiJz819tbAgx7nookYcCJZEaZKlofjjSHjoPBD41SmjWmn8PIrqdmT-bjrLWqqoR-Cm7WZC" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

