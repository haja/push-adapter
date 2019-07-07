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

package at.sbaresearch.microg.adapter.backend.registration.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.BuildConfig;
import at.sbaresearch.microg.adapter.backend.R;
import at.sbaresearch.microg.adapter.backend.gms.common.PackageUtils;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmDatabase;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs;
import at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterResponse;
import at.sbaresearch.microg.adapter.backend.registration.NullHostNameVerifier;
import at.sbaresearch.microg.adapter.backend.registration.app.HttpRegisterAppClient.AppRegisterRequest;
import at.sbaresearch.microg.adapter.backend.registration.app.HttpRegisterAppClient.AppRegisterResponse;
import at.sbaresearch.mqtt4android.pinning.ClientKeyCert;
import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import lombok.val;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

public class HttpRegisterAppService {
  private static final String TAG = "HttpRegAppSrv";
  private static final String RELAY_PUSH_PATH = "/push/";

  private static byte[] RELAY_CERT = null;

  private final HttpRegisterAppClient httpClient;

  public HttpRegisterAppService(Context context) throws Exception {
    setupStaticConsts(context);

    val mqtt = GcmPrefs.get(context).getMqttSettings();
    val keys = new ClientKeyCert(mqtt.getPrivKey(), mqtt.getCert());
    val factory = new PinningSslFactory(keys,
        context.getResources().openRawResource(R.raw.server));


    // normal okhttp pinning does not work with self signed certs
    OkHttpClient client = new OkHttpClient.Builder()
        .sslSocketFactory(factory.getSocketFactory(), factory.getTrustManager())
        .hostnameVerifier(new NullHostNameVerifier())
        .build();
    httpClient = new Retrofit.Builder()
        .baseUrl(BuildConfig.RELAY_HOST)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(client)
        .build()
        .create(HttpRegisterAppClient.class);
  }

  public interface BundleCallback {
    void onResult(Bundle bundle);
  }

  public void registerApp(Context context, final GcmDatabase database,
      String appPackageName, String senderId, final BundleCallback callback) {
    String appSignature = PackageUtils.firstSignatureDigest(context, appPackageName);

    AppRegisterRequest request = new AppRegisterRequest(appPackageName, appSignature, senderId);
    val registerCall = httpClient.registerApp(
        request);
    registerCall.enqueue(new Callback<AppRegisterResponse>() {
      @Override
      public void onResponse(Call<AppRegisterResponse> call,
          Response<AppRegisterResponse> response) {
        val old = AppRegisterResponse.toOldResponse(response);
        Log.i(TAG, "onResponse: " + response.code());
        callback.onResult(handleResponse(database, request, old));
      }

      @Override
      public void onFailure(Call<AppRegisterResponse> call, Throwable e) {
        Log.w(TAG, e);
        callback.onResult(handleErrorResponse(database, request, e, null));
      }
    });
  }

  private static Bundle handleResponse(GcmDatabase database, AppRegisterRequest request,
      RegisterResponse response) {
    if (response == null) {
      Bundle resultBundle = new Bundle();
      resultBundle.putString(EXTRA_ERROR, ERROR_SERVICE_NOT_AVAILABLE);
      return resultBundle;
    } else {
      database.noteAppRegistered(request.app, request.signature, response.token);
      return buildResultBundle(response);
    }
  }

  private static Bundle buildResultBundle(RegisterResponse response) {
    Bundle b = new Bundle();
    b.putString(EXTRA_REGISTRATION_ID, response.token);
    b.putString(EXTRA_RELAY_HOST, BuildConfig.RELAY_HOST + RELAY_PUSH_PATH);
    b.putByteArray(EXTRA_RELAY_CERT, RELAY_CERT);
    return b;
  }

  private static Bundle handleErrorResponse(GcmDatabase database, AppRegisterRequest request,
      Throwable e, String requestId) {
    Bundle resultBundle = new Bundle();
    if (e.getMessage() != null && e.getMessage().startsWith("Error=")) {
      String errorMessage = e.getMessage().substring(6);
      database.noteAppRegistrationError(request.app, errorMessage);

      resultBundle.putString(EXTRA_ERROR, attachRequestId(errorMessage, requestId));
    } else {
      resultBundle.putString(EXTRA_ERROR,
          attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
    }
    return resultBundle;
  }

  private static void setupStaticConsts(Context ctx) throws IOException {
    RELAY_CERT = readCert(ctx);
  }

  private static byte[] readCert(Context ctx) throws IOException {
    val certStream = ctx.getResources().openRawResource(R.raw.server);
    int b;
    val bos = new ByteArrayOutputStream();
    while((b = certStream.read()) != -1) {
      bos.write(b);
    }
    return bos.toByteArray();
  }

  public static String attachRequestId(String msg, String requestId) {
    if (requestId == null) return msg;
    return "|ID|" + requestId + "|" + msg;
  }
}
