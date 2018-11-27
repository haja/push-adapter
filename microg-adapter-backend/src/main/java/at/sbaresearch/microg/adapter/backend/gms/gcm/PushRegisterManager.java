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
import at.sbaresearch.microg.adapter.backend.gms.checkin.LastCheckinInfo;
import at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient;
import at.sbaresearch.microg.adapter.backend.gms.common.PackageUtils;
import at.sbaresearch.microg.adapter.backend.gms.common.Utils;
import at.sbaresearch.microg.adapter.backend.gms.gcm.PushRegisterClient.RegisterRequest2;
import at.sbaresearch.microg.adapter.backend.gms.gcm.PushRegisterClient.RegisterResponse2;
import lombok.val;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

public class PushRegisterManager {
  private static final String TAG = "GmsGcmRegisterMgr";

  private static PushRegisterClient pushRegisterClient;

  static {
    pushRegisterClient = new Retrofit.Builder()
        .baseUrl(PushRegisterClient.SERVICE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()
        .create(PushRegisterClient.class);
  }

  public static RegisterResponse unregister(Context context, String packageName,
      String pkgSignature, String sender, String info) {
    GcmDatabase database = new GcmDatabase(context);
    RegisterResponse response = new RegisterResponse();
    try {
      response = new RegisterRequest()
          .build(Utils.getBuild(context))
          .sender(sender)
          .info(info)
          .checkin(LastCheckinInfo.read(context))
          .app(packageName, pkgSignature)
          .delete(true)
          .getResponse();
    } catch (IOException e) {
      Log.w(TAG, e);
    }
    if (!packageName.equals(response.deleted)) {
      database.noteAppRegistrationError(packageName, response.responseText);
    } else {
      database.noteAppUnregistered(packageName, pkgSignature);
    }
    database.close();
    return response;
  }

  public interface BundleCallback {
    void onResult(Bundle bundle);
  }

  public static void completeRegisterRequest(Context context, GcmDatabase database,
      RegisterRequest request, BundleCallback callback) {
    completeRegisterRequest(context, database, null, request, callback);
  }

  public static void completeRegisterRequest(Context context, final GcmDatabase database,
      final String requestId, final RegisterRequest request, final BundleCallback callback) {
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
    if (!request.delete) {
      // TODO implement complex app registration logic?
//      if (!prefs.isEnabled() ||
//          (app != null && !app.allowRegister) ||
//          LastCheckinInfo.read(context).lastCheckin <= 0 ||
//          (app == null && prefs.isConfirmNewApps())) {
//        Bundle bundle = new Bundle();
//        bundle.putString(EXTRA_ERROR, ERROR_SERVICE_NOT_AVAILABLE);
//        callback.onResult(bundle);
//        return;
//      }
    } else {
      if (database.getRegistrationsByApp(request.app).isEmpty()) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_UNREGISTERED, attachRequestId(request.app, requestId));
        callback.onResult(bundle);
        return;
      }
    }

    val registerCall = pushRegisterClient.register(RegisterRequest2.fromOldRequest(request));
    registerCall.enqueue(new Callback<RegisterResponse2>() {
      @Override
      public void onResponse(Call<RegisterResponse2> call, Response<RegisterResponse2> response) {
        val old = RegisterResponse2.toOldResponse(response);
        callback.onResult(handleResponse(database, request, old, requestId));
      }

      @Override
      public void onFailure(Call<RegisterResponse2> call, Throwable e) {
        Log.w(TAG, e);
        callback.onResult(handleResponse(database, request, e, requestId));
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
      if (e.getMessage() != null && e.getMessage().startsWith("Error=")) {
        String errorMessage = e.getMessage().substring(6);
        database.noteAppRegistrationError(request.app, errorMessage);
        resultBundle.putString(EXTRA_ERROR, attachRequestId(errorMessage, requestId));
      } else {
        resultBundle
            .putString(EXTRA_ERROR, attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
      }
    } else {
      if (!request.delete) {
        if (response.token == null) {
          database.noteAppRegistrationError(request.app, response.responseText);
          resultBundle
              .putString(EXTRA_ERROR, attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
        } else {
          database.noteAppRegistered(request.app, request.appSignature, response.token);
          resultBundle.putString(EXTRA_REGISTRATION_ID, attachRequestId(response.token, requestId));
        }
      } else {
        if (!request.app.equals(response.deleted) && !request.app.equals(response.token)) {
          database.noteAppRegistrationError(request.app, response.responseText);
          resultBundle
              .putString(EXTRA_ERROR, attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
        } else {
          database.noteAppUnregistered(request.app, request.appSignature);
          resultBundle.putString(EXTRA_UNREGISTERED, attachRequestId(request.app, requestId));
        }
      }

      if (response.retryAfter != null && !response.retryAfter.contains(":")) {
        resultBundle.putLong(EXTRA_RETRY_AFTER, Long.parseLong(response.retryAfter));
      }
    }
    return resultBundle;
  }

  public static String attachRequestId(String msg, String requestId) {
    if (requestId == null) return msg;
    return "|ID|" + requestId + "|" + msg;
  }
}
