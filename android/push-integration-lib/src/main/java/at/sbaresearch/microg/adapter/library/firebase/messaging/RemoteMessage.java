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

package at.sbaresearch.microg.adapter.library.firebase.messaging;

import java.util.Map;

public class RemoteMessage {
  private final Map<String, String> data;
  private final String from;
  private final String messageId;
  private final long sentTime;

  public RemoteMessage(String from, Map<String, String> data, String messageId, long sentTime) {
    this.from = from;
    this.data = data;
    this.messageId = messageId;
    this.sentTime = sentTime;
  }

  /**
   * Gets the message payload data.
   */
  public Map<String, String> getData() {
    return data;
  }

  /**
   * Get the sender of this message.
   */
  public String getFrom() {
    return from;
  }

  /**
   * Gets the message's ID.
   */
  public String getMessageId() {
    return messageId;
  }

  public final long getSentTime() {
    return sentTime;
  }

}
