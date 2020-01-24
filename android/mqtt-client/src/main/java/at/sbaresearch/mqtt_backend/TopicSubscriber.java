/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html.
 */

package at.sbaresearch.mqtt_backend;

import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class TopicSubscriber {
  private static final String TAG = "TopicSubscriber";

  private final MqttAndroidClient client;
  private final ConnectionSettings connectionSettings;

  public TopicSubscriber(MqttAndroidClient client, ConnectionSettings connectionSettings) {
    this.client = client;
    this.connectionSettings = connectionSettings;
  }

  public void subscribe() {
    try {
      client.subscribe(connectionSettings.getTopic(), 0).setActionCallback(
          new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
              Log.i(TAG, "subscribe success");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable e) {
              Log.e(TAG, "subscribe failed", e);
            }
          });
    } catch (
        MqttException e) {
      Log.e(TAG, "subscribe failed: " + e.getMessage());
    }
  }
}
