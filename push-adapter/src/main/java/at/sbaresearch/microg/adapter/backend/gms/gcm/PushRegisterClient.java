package at.sbaresearch.microg.adapter.backend.gms.gcm;

import at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient.ResponseField;
import at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient.ResponseHeader;
import at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient.ResponseStatusText;
import lombok.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PushRegisterClient {
  String SERVICE_URL = "http://10.0.2.2:9876";

  @POST("registration/new")
  Call<RegisterResponse2> register(@Body RegisterRequest2 request);

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class RegisterRequest2 {
    String app;
    String cert;
    int appVer;
    String appVerName;
    String info;

    // TODO use new requests only
    public static RegisterRequest2 fromOldRequest(RegisterRequest old) {
      return new RegisterRequest2(old.app, old.appSignature, old.appVersion, old.appVersionName,
          old.info);
    }
  }

  @Data
  @NoArgsConstructor
  class RegisterResponse2 {
    String token;
    String deleted;

    public static RegisterResponse toOldResponse(Response<RegisterResponse2> resp) {
      val old = new RegisterResponse();
      old.responseText = resp.message();

      val body = resp.body();
      if (body != null) {
        old.deleted = body.deleted;
        old.token = body.token;
        old.retryAfter = null; // TODO retryAfter not implemented
      }
      return old;
    }
  }

}
