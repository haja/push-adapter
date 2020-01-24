/*
 * Copyright (C) 2013-2017, 2020 microG Project Team, Harald Jagenteufel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
