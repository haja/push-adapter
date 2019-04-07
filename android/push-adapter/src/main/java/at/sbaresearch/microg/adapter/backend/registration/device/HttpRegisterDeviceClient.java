package at.sbaresearch.microg.adapter.backend.registration.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpRegisterDeviceClient {
  // TODO externalize config
  String SERVICE_HOSTNAME = "trigger.lan";
  String SERVICE_URL = "https://" + SERVICE_HOSTNAME + ":9876";

  @POST("registration/device")
  Call<DeviceRegisterResponse> registerDevice(@Body DeviceRegisterRequest request);

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
