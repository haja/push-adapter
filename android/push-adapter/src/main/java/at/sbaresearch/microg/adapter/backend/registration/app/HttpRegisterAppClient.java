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

package at.sbaresearch.microg.adapter.backend.registration.app;

import at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterResponse;
import lombok.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpRegisterAppClient {
  // TODO externalize config
  String SERVICE_URL = "https://trigger.lan:9876";

  @POST("registration/new")
  Call<AppRegisterResponse> registerApp(@Body AppRegisterRequest request);

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class AppRegisterRequest {
    String app;
    String signature;
    String senderId;
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
