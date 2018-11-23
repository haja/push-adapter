/*
 * Copyright (C) 2013-2017 microG Project Team
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

import at.sbaresearch.microg.adapter.backend.gms.checkin.LastCheckinInfo;
import at.sbaresearch.microg.adapter.backend.gms.common.Build;
import at.sbaresearch.microg.adapter.backend.gms.common.Constants;
import at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient;
import at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient.Request;

import java.io.IOException;

import static at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient.RequestContent;
import static at.sbaresearch.microg.adapter.backend.gms.common.HttpFormClient.RequestHeader;

public class RegisterRequest extends Request {
  //private static final String SERVICE_URL = "https://android.clients.google.com/c2dm/register3";
  private static final String SERVICE_URL = "http://10.0.2.2:8080/registration/new";
  private static final String USER_AGENT = "Android-GCM/1.3 (%s %s)";

  @RequestHeader("Authorization")
  private String auth;
  @RequestHeader("User-Agent")
  private String userAgent;

  @RequestHeader("app")
  @RequestContent("app")
  public String app;
  @RequestContent("cert")
  public String appSignature;
  @RequestContent("app_ver")
  public int appVersion;
  @RequestContent("app_ver_name")
  public String appVersionName;
  @RequestContent("info")
  public String info;
  @RequestContent({"sender", "subtype"})
  public String sender;
  @RequestContent({"X-GOOG.USER_AID", "device"})
  public long androidId;
  @RequestContent("delete")
  public boolean delete;
  public long securityToken;
  public String deviceName;
  public String buildVersion;
  @RequestContent("osv")
  public int sdkVersion;
  @RequestContent("gmsv")
  public int gmsVersion;
  @RequestContent("scope")
  public String scope = "*";
  @RequestContent("appid")
  public String appId;
  @RequestContent("gmp_app_id")
  public String gmpAppId;

  @Override
  public void prepare() {
    userAgent = String.format(USER_AGENT, deviceName, buildVersion);
    auth = "AidLogin " + androidId + ":" + securityToken;
    gmsVersion = Constants.MAX_REFERENCE_VERSION;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest checkin(
      LastCheckinInfo lastCheckinInfo) {
    androidId = lastCheckinInfo.androidId;
    securityToken = lastCheckinInfo.securityToken;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest app(String app) {
    this.app = app;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest app(
      String app, String appSignature) {
    this.app = app;
    this.appSignature = appSignature;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest app(
      String app, String appSignature, int appVersion, String appVersionName) {
    this.app = app;
    this.appSignature = appSignature;
    this.appVersion = appVersion;
    this.appVersionName = appVersionName;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest appid(
      String appid, String gmpAppId) {
    this.appId = appid;
    this.gmpAppId = gmpAppId;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest info(String info) {
    this.info = info;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest sender(String sender) {
    this.sender = sender;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest build(Build build) {
    deviceName = build.device;
    buildVersion = build.id;
    sdkVersion = build.sdk;
    return this;
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest delete() {
    return delete(true);
  }

  public at.sbaresearch.microg.adapter.backend.gms.gcm.RegisterRequest delete(boolean delete) {
    this.delete = delete;
    return this;
  }

  public RegisterResponse getResponse() throws IOException {
    return HttpFormClient.request(SERVICE_URL, this, RegisterResponse.class);
  }

  public void getResponseAsync(HttpFormClient.Callback<RegisterResponse> callback) {
    HttpFormClient.requestAsync(SERVICE_URL, this, RegisterResponse.class, callback);
  }
}
