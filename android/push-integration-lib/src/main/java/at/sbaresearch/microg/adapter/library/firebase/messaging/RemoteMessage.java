package at.sbaresearch.microg.adapter.library.firebase.messaging;

import java.util.Map;

public class RemoteMessage {
  private final Map<String, String> data;
  private final String from;

  public RemoteMessage(String from, Map<String, String> data) {
    this.from = from;
    this.data = data;
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

    return "";
  }

}
