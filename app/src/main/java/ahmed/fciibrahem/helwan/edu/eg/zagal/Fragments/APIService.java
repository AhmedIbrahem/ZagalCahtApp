package ahmed.fciibrahem.helwan.edu.eg.zagal.Fragments;

import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.MyResponse;
import ahmed.fciibrahem.helwan.edu.eg.zagal.Notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAK3Ynjd0:APA91bGa5fwn3W7VXzyFbbR-hJtYgahGOmbpNF00f7kFF3AdeBv30Ur-BslbGhDO9NJmxVl2MUaPUyUgXLY7iDZCQcrcmTmXRUlexwRTcceCR-Er0r3yR1FckcRjjzyLlut288e6Sh-6"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
