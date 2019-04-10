package at.sbaresearch.microg.adapter.backend.registration.app;

import at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterResponse;
import lombok.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpRegisterAppClient {
  // TODO externalize config
  String SERVICE_HOSTNAME = "trigger.lan";
  String SERVICE_URL = "https://" + SERVICE_HOSTNAME + ":9876";

  @POST("registration/new")
  Call<AppRegisterResponse> registerApp(@Body AppRegisterRequest request);

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class AppRegisterRequest {
    String app;
    String signature;
  }

  @Data
  @NoArgsConstructor
  class AppRegisterResponse {
    String token;

    public static RegisterResponse toOldResponse(Response<AppRegisterResponse> resp) {
      val old = new RegisterResponse();
      old.responseText = resp.message();

      val body = resp.body();
      if (body != null) {
        old.token = body.token;
      }
      return old;
    }
  }
}
