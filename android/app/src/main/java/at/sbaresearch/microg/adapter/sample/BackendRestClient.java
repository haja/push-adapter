package at.sbaresearch.microg.adapter.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackendRestClient {

  @POST("register")
  Call<Void> sendRegistrationId(@Body AppRegistrationRequest req);

  @POST("send")
  Call<Void> sendMessage(@Body String message);

  @Data
  @AllArgsConstructor
  class AppRegistrationRequest {
    private String registrationId;
    private String relayUrl;
    private byte[] relayCert;
  }
}
