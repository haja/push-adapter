package at.sbaresearch.microg.adapter.backend.gms.gcm;

import lombok.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PushRegisterClient {
  // TODO externalize config
  String SERVICE_HOSTNAME = "trigger.lan";
  String SERVICE_URL = "https://" + SERVICE_HOSTNAME + ":9876";

  @POST("registration/new")
  Call<AppRegisterResponse> registerApp(@Body AppRegisterRequest request);

  @POST("registration/device")
  Call<DeviceRegisterResponse> registerDevice(@Body DeviceRegisterRequest request);

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class AppRegisterRequest {
    String app;
    String cert;
    int appVer;
    String appVerName;
    String info;

    // TODO use new requests only
    public static AppRegisterRequest fromOldRequest(RegisterRequest old) {
      return new AppRegisterRequest(old.app, old.appSignature, old.appVersion, old.appVersionName,
          old.info);
    }
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
        old.retryAfter = null; // TODO retryAfter not implemented
      }
      return old;
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeviceRegisterRequest {
    String dummy = "dummy";
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeviceRegisterResponse {
    @NonNull
    String host;
    @NonNull
    Integer port;
    @NonNull
    String mqttTopic;
    @NonNull
    byte[] encodedPrivateKey;
    @NonNull
    byte[] encodedCert;
  }
}
