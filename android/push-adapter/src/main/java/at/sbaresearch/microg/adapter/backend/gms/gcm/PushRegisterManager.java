/*
 * Copyright (C) 2018 microG Project Team
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

package at.sbaresearch.microg.adapter.backend.gms.gcm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.MqttClientAdapter;
import at.sbaresearch.microg.adapter.backend.gms.common.PackageUtils;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs.MqttSettings;
import at.sbaresearch.microg.adapter.backend.gms.gcm.PushRegisterClient.AppRegisterRequest;
import at.sbaresearch.microg.adapter.backend.gms.gcm.PushRegisterClient.AppRegisterResponse;
import at.sbaresearch.microg.adapter.backend.gms.gcm.PushRegisterClient.DeviceRegisterRequest;
import at.sbaresearch.microg.adapter.backend.gms.gcm.PushRegisterClient.DeviceRegisterResponse;
import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import lombok.val;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

public class PushRegisterManager {
  private static final String TAG = "GmsGcmRegisterMgr";

  private static PushRegisterClient pushRegisterClient;

  static {
    // TODO normal okhttp pinning does not work with self signed certs
    val factory = new PinningSslFactory()
    val client = new OkHttpClient.Builder()
        // TODO set socket factory
        .sslSocketFactory()
        .build();
    pushRegisterClient = new Retrofit.Builder()
        .baseUrl(PushRegisterClient.SERVICE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(client)
        .build()
        .create(PushRegisterClient.class);
  }

  public interface BundleCallback {
    void onResult(Bundle bundle);
  }

  public static void registerDevice(Context context) {
    val registerCall =  pushRegisterClient.registerDevice(new DeviceRegisterRequest("foobar"));
    registerCall.enqueue(new Callback<DeviceRegisterResponse>() {
      @Override
      public void onResponse(Call<DeviceRegisterResponse> call, Response<DeviceRegisterResponse> response) {
        Log.i(TAG, "onDeviceRegResponse: " + response.code());
        val resp = response.body();
        if(resp == null) {
          Log.w(TAG, "deviceResponse was null");
          return;
        }

        val prefs = GcmPrefs.get(context);
        val settings = fromRequest(resp.host, resp.port, resp.mqttTopic,
            resp.encodedPrivateKey, resp.encodedCert);
        prefs.setMqttSettings(settings);

        MqttClientAdapter.ensureBackendConnection(context);
      }

      private MqttSettings fromRequest(String host, Integer port, String topic,
          byte[] encodedPrivateKey, byte[] encodedCert) {
        return new MqttSettings(host, port, topic, encodedPrivateKey, encodedCert);
      }

      @Override
      public void onFailure(Call<DeviceRegisterResponse> call, Throwable e) {
        Log.w(TAG, "onDeviceReg failed", e);
      }
    });
  }

  public static void completeRegisterRequest(Context context, final GcmDatabase database,
      final RegisterRequest request, final BundleCallback callback) {
    if (request.app != null) {
      if (request.appSignature == null)
        request.appSignature = PackageUtils.firstSignatureDigest(context, request.app);
      if (request.appVersion <= 0)
        request.appVersion = PackageUtils.versionCode(context, request.app);
      if (request.appVersionName == null)
        request.appVersionName = PackageUtils.versionName(context, request.app);
    }

    GcmDatabase.App app = database.getApp(request.app);
    GcmPrefs prefs = GcmPrefs.get(context);
    if (database.getRegistrationsByApp(request.app).isEmpty()) {
      Bundle bundle = new Bundle();
      bundle.putString(EXTRA_UNREGISTERED, attachRequestId(request.app, null));
      callback.onResult(bundle);
      return;
    }

    val registerCall = pushRegisterClient.registerApp(AppRegisterRequest.fromOldRequest(request));
    registerCall.enqueue(new Callback<AppRegisterResponse>() {
      @Override
      public void onResponse(Call<AppRegisterResponse> call, Response<AppRegisterResponse> response) {
        val old = AppRegisterResponse.toOldResponse(response);
        Log.i(TAG, "onResponse: " + response.code());
        callback.onResult(handleResponse(database, request, old, null));
      }

      @Override
      public void onFailure(Call<AppRegisterResponse> call, Throwable e) {
        Log.w(TAG, e);
        callback.onResult(handleResponse(database, request, e, null));
      }
    });
  }

  private static Bundle handleResponse(GcmDatabase database, RegisterRequest request,
      RegisterResponse response, String requestId) {
    return handleResponse(database, request, response, null, requestId);
  }

  private static Bundle handleResponse(GcmDatabase database, RegisterRequest request, Throwable e,
      String requestId) {
    return handleResponse(database, request, null, e, requestId);
  }

  private static Bundle handleResponse(GcmDatabase database, RegisterRequest request,
      RegisterResponse response, Throwable e, String requestId) {
    Bundle resultBundle = new Bundle();
    if (response == null && e == null) {
      resultBundle.putString(EXTRA_ERROR, attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
    } else if (e != null) {
      handleErrorResponse(database, request, e, requestId, resultBundle);
    } else {
      handleSuccessResponse(database, request, response, requestId, resultBundle);
    }
    return resultBundle;
  }

  private static void handleSuccessResponse(GcmDatabase database, RegisterRequest request,
      RegisterResponse response, String requestId, Bundle resultBundle) {
    handleRegisterResponse(database, request, response, requestId, resultBundle);

    if (response.retryAfter != null && !response.retryAfter.contains(":")) {
      resultBundle.putLong(EXTRA_RETRY_AFTER, Long.parseLong(response.retryAfter));
    }
  }

  private static void handleRegisterResponse(GcmDatabase database, RegisterRequest request,
      RegisterResponse response, String requestId, Bundle resultBundle) {
    if (response.token == null) {
      database.noteAppRegistrationError(request.app, response.responseText);
      resultBundle
          .putString(EXTRA_ERROR, attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
    } else {
      database.noteAppRegistered(request.app, request.appSignature, response.token);
      resultBundle.putString(EXTRA_REGISTRATION_ID, attachRequestId(response.token, requestId));
    }
  }

  private static void handleErrorResponse(GcmDatabase database, RegisterRequest request,
      Throwable e, String requestId, Bundle resultBundle) {
    if (e.getMessage() != null && e.getMessage().startsWith("Error=")) {
      String errorMessage = e.getMessage().substring(6);
      database.noteAppRegistrationError(request.app, errorMessage);
      resultBundle.putString(EXTRA_ERROR, attachRequestId(errorMessage, requestId));
    } else {
      resultBundle
          .putString(EXTRA_ERROR, attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
    }
  }

  public static String attachRequestId(String msg, String requestId) {
    if (requestId == null) return msg;
    return "|ID|" + requestId + "|" + msg;
  }
}
