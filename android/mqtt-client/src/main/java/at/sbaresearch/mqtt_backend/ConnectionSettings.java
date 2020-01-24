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

import lombok.ToString;
import lombok.Value;

@Value
@ToString(exclude = {"privKey", "cert"})
public class ConnectionSettings {
  String host;
  int port;
  String topic;
  byte[] privKey;
  byte[] cert;

  public String getServerUrl() {
    return "ssl://" + host + ":" + port;
  }

}
