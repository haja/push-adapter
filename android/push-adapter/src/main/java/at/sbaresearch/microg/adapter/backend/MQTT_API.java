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

package at.sbaresearch.microg.adapter.backend;

public class MQTT_API {
  public static final String app = "app";
  public static final String signature = "sig";
  public static final String messageId = "messageId";
  public static final String payload = "payload";
  public static final String sentTime = "sentTime";
  public static final String senderId = "senderId";

  public static final String INTENT_MQTT_CONNECT =
      "at.sbaresearch.android.gcm.mqtt.intent.CONNECT";
  public static final String INTENT_MQTT_CONNECT_HOST = "host";
  public static final String INTENT_MQTT_CONNECT_PORT = "port";
  public static final String INTENT_MQTT_CONNECT_TOPIC = "topic";
  public static final String INTENT_MQTT_CONNECT_CLIENT_KEY = "clientkey";
  public static final String INTENT_MQTT_CONNECT_CLIENT_CERT = "clientcert";

  public static final String PERMISSION_SEND = "at.sbaresearch.android.gcm.mqtt.intent.SEND";
}
